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

package w3c.n3dev.parser.event;

import java.net.URI;
import java.net.URISyntaxException;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.impl.TermUtil;

import w3c.n3dev.parser.n3ParserErrorListener;
import w3c.n3dev.parser.n3PrefixException;
import w3c.n3dev.parser.n3PrefixException.PrefixErrors;
import w3c.n3dev.parser.antlr.n3BaseListener;
import w3c.n3dev.parser.antlr.n3Parser;

public abstract class n3EventHandler extends n3BaseListener {

	protected URI explicitBaseUri = null;
	protected URI docUri = null;
	protected N3ModelSpec spec;

	protected n3ParserErrorListener listener;

	public n3EventHandler(String docUri, n3ParserErrorListener listener) {
		try {
			if (docUri != null)
				this.docUri = new URI(TermUtil.dropFragment(docUri) + "#");

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.listener = listener;
	}

	@Override
	public void exitPrefixID(n3Parser.PrefixIDContext ctx) {
		processPrefix(ctx.PNAME_NS(), ctx.IRIREF());
	}

	@Override
	public void exitSparqlPrefix(n3Parser.SparqlPrefixContext ctx) {
		processPrefix(ctx.PNAME_NS(), ctx.IRIREF());
	}

	private void processPrefix(TerminalNode pNameNs, TerminalNode iriRef) {
		if (pNameNs == null)
			return;

		String prefix = pNameNs.getText().trim();
		prefix = prefix.substring(0, prefix.length() - 1);

		String uri = iri(iriRef);

		setPrefix(prefix, uri);
	}

	@Override
	public void exitBase(n3Parser.BaseContext ctx) {
		processBase(ctx.IRIREF());
	}

	@Override
	public void exitSparqlBase(n3Parser.SparqlBaseContext ctx) {
		processBase(ctx.IRIREF());
	}

	private void processBase(TerminalNode iriRef) {
		String baseUri = iriRef.getText().trim();
		baseUri = baseUri.substring(1, baseUri.length() - 1);

		try {
			this.explicitBaseUri = new URI(baseUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		setBase(baseUri);
	}

	@Override
	public void exitPrefixedName(n3Parser.PrefixedNameContext ctx) {
		TerminalNode pNameLn = ctx.PNAME_LN();

		if (pNameLn != null) {
			String pName = pNameLn.getText().trim();

			int idx = pName.indexOf(":");
			String prefix = pName.substring(0, idx).trim();
			String name = pName.substring(idx + 1);

			String ns = tryGetNs(prefix, pName);
			if (ns != null) {
				String iri = ns + name;
				setIri(iri);
			}

		} else {
			String pNameNs = ctx.PNAME_NS().getText().trim();
			// e.g., "rdfs:"
			String prefix = pNameNs.substring(0, pNameNs.length() - 1);

			String ns = tryGetNs(prefix, pNameNs);
			if (ns != null)
				setIri(ns);
		}
	}

	private String tryGetNs(String prefix, String original) {
		String ns = getPrefix(prefix);
		if (ns == null)
			listener.prefixError(prefix, original, new n3PrefixException(PrefixErrors.UNKNOWN_PREFIX));

		return ns;
	}

	protected abstract void setBase(String uri);

	protected abstract void setPrefix(String prefix, String uri);

	protected abstract String getPrefix(String prefix);

	protected abstract void setIri(String iri);

	protected boolean hasBaseUri() {
		return explicitBaseUri != null || docUri != null;
	}

	protected URI getBaseUri() {
		return (explicitBaseUri != null ? explicitBaseUri : docUri);
	}

	protected String text(ParseTree node) {
		if (node == null)
			return null;
		return node.getText().trim();
	}

	protected String text(Token token) {
		if (token == null)
			return null;
		return token.getText().trim();
	}

	protected String iri(TerminalNode n) {
		String s = text(n);
		s = s.substring(1, s.length() - 1);

		if (getBaseUri() != null)
			s = TermUtil.resolveToBase(s, getBaseUri());

		return s.replaceAll("\\s", "");
	}

	protected String collectText(RuleContext ctx, String separator) {
		StringBuffer str = new StringBuffer();

		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (str.length() > 0)
				str.append(separator);
			str.append(ctx.getChild(i));
		}

		return str.toString();
	}
}
