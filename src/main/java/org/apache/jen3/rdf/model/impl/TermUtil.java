/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.rdf.model.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jen3.JenaRuntime;
import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.ext.xerces.util.XMLChar;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.shared.CannotEncodeCharacterException;
import org.apache.jen3.util.SplitIRI;

/**
 * Some utility functions.
 */
public class TermUtil extends Object {

	/**
	 * Given an absolute URI, determine the split point between the namespace part
	 * and the localname part. If there is no valid localname part then the length
	 * of the string is returned. The algorithm tries to find the longest NCName at
	 * the end of the uri, not immediately preceeded by the first colon in the
	 * string.
	 * <p>
	 * This operation follows XML QName rules which are more complicated than needed
	 * for Turtle and TriG. For example, QName can't start with a digit.
	 * 
	 * @param uri
	 * @return the index of the first character of the localname
	 * @see SplitIRI
	 */
	public static int splitNamespaceXML(String uri) {

		// XML Namespaces 1.0:
		// A qname name is NCName ':' NCName
		// NCName ::= NCNameStartChar NCNameChar*
		// NCNameChar ::= NameChar - ':'
		// NCNameStartChar ::= Letter | '_'
		//
		// XML 1.0
		// NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] |
		// [#xD8-#xF6] | [#xF8-#x2FF] |
		// [#x370-#x37D] | [#x37F-#x1FFF] |
		// [#x200C-#x200D] | [#x2070-#x218F] |
		// [#x2C00-#x2FEF] | [#x3001-#xD7FF] |
		// [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
		// NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 |
		// [#x0300-#x036F] | [#x203F-#x2040]
		// Name ::= NameStartChar (NameChar)*

		char ch;
		int lg = uri.length();
		if (lg == 0)
			return 0;
		int i = lg - 1;
		for (; i >= 1; i--) {
			ch = uri.charAt(i);
			if (notNameChar(ch))
				break;
		}

		int j = i + 1;

		if (j >= lg)
			return lg;

		// Check we haven't split up a %-encoding.
		if (j >= 2 && uri.charAt(j - 2) == '%')
			j = j + 1;
		if (j >= 1 && uri.charAt(j - 1) == '%')
			j = j + 2;

		// Have found the leftmost NCNameChar from the
		// end of the URI string.
		// Now scan forward for an NCNameStartChar
		// The split must start with NCNameStart.
		for (; j < lg; j++) {
			ch = uri.charAt(j);
//            if (XMLChar.isNCNameStart(ch))
//                break ;
			if (XMLChar.isNCNameStart(ch)) {
				// "mailto:" is special.
				// Keep part after mailto: at least one charcater.
				// Do a quick test before calling .startsWith
				// OLD: if ( uri.charAt(j - 1) == ':' && uri.lastIndexOf(':', j - 2) == -1)
				if (j == 7 && uri.startsWith("mailto:"))
					continue; // split "mailto:me" as "mailto:m" and "e" !
				else
					break;
			}
		}
		return j;
	}

	public static String getLocalName(String uri) {
		return uri.substring(splitNamespaceXML(uri));
	}

	/**
	 * answer true iff this is not a legal NCName character, ie, is a possible
	 * split-point start.
	 */
	public static boolean notNameChar(char ch) {
		return !XMLChar.isNCName(ch);
	}

	protected static Pattern standardEntities = Pattern.compile("&|<|>|\t|\n|\r|\'|\"");

	public static String substituteStandardEntities(String s) {
		if (standardEntities.matcher(s).find()) {
			return substituteEntitiesInElementContent(s).replaceAll("'", "&apos;").replaceAll("\t", "&#9;")
					.replaceAll("\n", "&#xA;").replaceAll("\r", "&#xD;").replaceAll("\"", "&quot;");
		} else
			return s;
	}

	protected static Pattern entityValueEntities = Pattern.compile("&|%|\'|\"");

	public static String substituteEntitiesInEntityValue(String s) {
		if (entityValueEntities.matcher(s).find()) {
			return s.replaceAll("&", "&amp;").replaceAll("'", "&apos;").replaceAll("%", "&#37;").replaceAll("\"",
					"&quot;");
		} else
			return s;
	}

	protected static Pattern elementContentEntities = Pattern.compile("<|>|&|[\0-\37&&[^\n\t]]|\uFFFF|\uFFFE");

	/**
	 * Answer <code>s</code> modified to replace &lt;, &gt;, and &amp; by their
	 * corresponding entity references.
	 * 
	 * <p>
	 * Implementation note: as a (possibly misguided) performance hack, the obvious
	 * cascade of replaceAll calls is replaced by an explicit loop that looks for
	 * all three special characters at once.
	 */
	public static String substituteEntitiesInElementContent(String s) {
		Matcher m = elementContentEntities.matcher(s);
		if (!m.find())
			return s;
		else {
			int start = 0;
			StringBuilder result = new StringBuilder();
			do {
				result.append(s.substring(start, m.start()));
				char ch = s.charAt(m.start());
				switch (ch) {
				case '\r':
					result.append("&#xD;");
					break;
				case '<':
					result.append("&lt;");
					break;
				case '&':
					result.append("&amp;");
					break;
				case '>':
					result.append("&gt;");
					break;
				default:
					throw new CannotEncodeCharacterException(ch, "XML");
				}
				start = m.end();
			} while (m.find(start));
			result.append(s.substring(start));
			return result.toString();
		}
	}

	public static String replace(String s, String oldString, String newString) {
		return s.replace(oldString, newString);
	}

	/**
	 * A Node is a simple string if:
	 * <li>(RDF 1.0) No datatype and no language tag.
	 * <li>(RDF 1.1) xsd:string
	 */
	public static boolean isSimpleString(Node n) {
		Objects.requireNonNull(n);
		if (!n.isLiteral())
			return false;
		RDFDatatype dt = n.getLiteralDatatype();
		if (dt == null)
			return !isLangString(n);
		if (JenaRuntime.isRDF11)
			return dt.equals(XSDDatatype.XSDstring);
		return false;
	}

	/**
	 * A Node is a language string if it has a language tag. (RDF 1.0 and RDF 1.1)
	 */
	public static boolean isLangString(Node n) {
		Objects.requireNonNull(n);
		if (!n.isLiteral())
			return false;
		String lang = n.getLiteralLanguage();
		if (lang == null)
			return false;
		return !lang.equals("");
	}

	/**
	 * Return true if the literal is a simple string.
	 * <p>
	 * RDF 1.0 {@literal =>} it is a plain literal, with no language tag
	 * <p>
	 * RDF 1.1 {@literal =>} it has datatype xsd:string
	 */
	public static boolean isSimpleString(Literal lit) {
		Objects.requireNonNull(lit);
		String dtStr = lit.getDatatypeURI();
		if (dtStr == null)
			return !isLangString(lit);
		if (JenaRuntime.isRDF11)
			return dtStr.equals(XSDDatatype.XSDstring.getURI());
		return false;
	}

	/** Return true if the literal has a language tag. (RDF 1.0 and RDF 1.1) */
	public static boolean isLangString(Literal lit) {
		Objects.requireNonNull(lit);
		String lang = lit.getLanguage();
		if (lang == null)
			return false;
		return !lang.equals("");
	}

	// https://tools.ietf.org/html/rfc3986#section-5.3

	public static String resolveToBase(String uri, URI baseUriObj) {
		try {
			URI uriObj = new URI(uri);
			if (uriObj.getScheme() != null) {
				return uri;

			} else {
				String scheme = baseUriObj.getScheme(), authority, path, query, fragment = uriObj.getFragment();

				if (isDefined(uriObj.getAuthority())) {
					authority = uriObj.getAuthority();
					path = uriObj.getPath();
					query = uriObj.getQuery();

				} else {
					authority = baseUriObj.getAuthority();

					if (!isDefined(uriObj.getPath())) {
						path = baseUriObj.getPath();

						if (!isDefined(uriObj.getQuery()))
							query = baseUriObj.getQuery();
						else
							query = uriObj.getQuery();

					} else {
						query = uriObj.getQuery();

						if (uriObj.getPath().startsWith("/"))
							path = uriObj.getPath();
						else {
							// merge
							if (isDefined(baseUriObj.getAuthority()) && !isDefined(baseUriObj.getPath()))
								path = "/" + uriObj.getPath();
							else {
								int slIdx = baseUriObj.getPath().lastIndexOf("/");
								if (slIdx == -1)
									path = uriObj.getPath();
								else {
									path = baseUriObj.getPath().substring(0, slIdx + 1) + uriObj.getPath();
								}
							}
						}
					}
				}

				// we always want "//" for file uris,
				// which are only appended if authority is present

				// so, no nulls..

				if (isDefined(scheme) && scheme.equals("file") && authority == null)
					authority = "";

				URI ret = new URI(scheme, authority, path, query, fragment);
				return ret.toString();
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
			return uri;
		}
	}

	private static boolean isDefined(String value) {
		return value != null && !value.isEmpty();
	}

	public static String getFileUri(String path) {
		return "file://" + path.replace("\\", "/");
	}

	public static String dropFragment(String uri) {
		if (uri.endsWith("#") || uri.endsWith("/"))
			return uri.substring(0, uri.length() - 1);
		else
			return uri;
	}

//	public static void main(String[] args) throws Exception {
////		String uri = "concatenation.n3#";
//		String uri = "example.org/concatenation.n3";
////		String uri = "#";
//
//		String baseUri = "file://D:/git/n3dev/jena-core/testing/N3/jen3_reason/concatenation-out.n3";
//
//		System.out.println(Util.resolveToBase(uri, new URI(baseUri)));
////		System.out.println(new URI())
//	}
}
