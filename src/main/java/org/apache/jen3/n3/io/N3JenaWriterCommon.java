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

package org.apache.jen3.n3.io;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jen3.JenaRuntime;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_Literal;
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.graph.n3.Node_Quantifier;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.AnonId;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.Property;
import org.apache.jen3.rdf.model.QuantifiedVariable;
import org.apache.jen3.rdf.model.Quantifier;
import org.apache.jen3.rdf.model.RDFErrorHandler;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.RDFWriter;
import org.apache.jen3.rdf.model.ResIterator;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.shared.JenaException;
import org.apache.jen3.util.iterator.ClosableIterator;
import org.apache.jen3.util.iterator.WrappedIterator;
import org.apache.jen3.vocabulary.N3;
import org.apache.jen3.vocabulary.N3Log;
import org.apache.jen3.vocabulary.OWL;
import org.apache.jen3.vocabulary.RDF;
import org.apache.jen3.vocabulary.RDFS;
import org.apache.jen3.vocabulary.XSD;
import org.apache.jena.ext.com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common framework for implementing N3 writers.
 */

public class N3JenaWriterCommon implements RDFWriter {

	static Logger logger = LoggerFactory.getLogger(N3JenaWriterCommon.class);

	// N3 writing proceeds in 2 stages.
	// First, it analysis the model to be written to extract information
	// that is going to be specially formatted (RDF lists, one ref anon nodes)
	// Second do the writing walk.

	// The simple N3 writer does nothing during preparation.

	protected Map<String, Object> writerPropertyMap = null;

// BaseURI - <#>    
//    final boolean doAbbreviatedBaseURIref = getBooleanValue("abbrevBaseURI", true) ; 
	protected boolean alwaysAllocateBNodeLabel = false;

	// Common variables
	protected RDFErrorHandler errorHandler = null;

	// Specific properties that have a short form.
	// Not Turtle.
	protected static final String NS_W3_log = "http://www.w3.org/2000/10/swap/log#";

	protected N3ModelSpec spec;

	protected static Map<String, String> wellKnownPropsMapN3 = new HashMap<>();
	static {
		wellKnownPropsMapN3.put(NS_W3_log + "implies", "=>");
		wellKnownPropsMapN3.put(OWL.sameAs.getURI(), "=");
		wellKnownPropsMapN3.put(RDF.type.getURI(), "a");
	}

	protected static Set<Resource> ignorePaddingPropsN3 = new HashSet<>();
	static {
		ignorePaddingPropsN3.add(N3Log.implies);
	}

	protected static Map<String, String> wellKnownPropsMapTurtle = new HashMap<>();
	static {
		wellKnownPropsMapN3.put(RDF.type.getURI(), "a");
	}

	protected Map<String, String> wellKnownPropsMap = wellKnownPropsMapN3;

	protected static Map<String, String> wellKnownPrefixesMap = new HashMap<>();
	static {
		wellKnownPrefixesMap.put("rdf", RDF.uri);
		wellKnownPrefixesMap.put("rdfs", RDFS.uri);
		wellKnownPrefixesMap.put("log", N3.log);
		wellKnownPrefixesMap.put("math", N3.math);
		wellKnownPrefixesMap.put("list", N3.list);
		wellKnownPrefixesMap.put("in", N3.in);
		wellKnownPrefixesMap.put("xsd", XSD.NS);
	}

	protected Model model;
	protected Map<String, String> prefixMap = new HashMap<>(); // Prefixes to actually use
	protected Map<String, String> reversePrefixMap = new HashMap<>(); // URI->prefix

	// Work variables controlling the output
	protected N3IndentedWriter out = null;
	// Removed base URI specials - look for "// BaseURI - <#>" &
	// doAbbreviatedBaseURIref
	// String baseURIref = null ;
	// String baseURIrefHash = null ;

	// Min spacing of items
	protected int minGap = getIntValue("minGap", 1);
	protected String minGapStr = pad(minGap);

	// Gap from subject to property
	protected int indentProperty = getIntValue("indentProperty", 4);

	// Width of property before wrapping.
	// This is not necessarily a control of total width
	// e.g. the pretty writer may be writing properties inside indented one ref
	// bNodes
	protected int widePropertyLen = getIntValue("widePropertyLen", 20);

	// Column for property when an object follows a property on the same line
	protected int propertyCol = getIntValue("propertyColumn", 4);

	// Minimum gap from property to object when object on a new line.
	protected int indentObject = propertyCol;

	// If a subject is shorter than this, the first property may go on same line.
	protected int subjectColumn = getIntValue("subjectColumn", indentProperty + 2);
	// Require shortSubject < subjectCol (strict less than)
	protected int shortSubject = subjectColumn - minGap;

	protected int curIndent = 0;
	protected Resource curPredicate = null;

	protected boolean useWellKnownPropertySymbols = getBooleanValue("usePropertySymbols", true);

	protected boolean allowTripleQuotedStrings = getBooleanValue("useTripleQuotedStrings", true);
	protected boolean allowDoubles = getBooleanValue("useDoubles", true);
	protected boolean allowDecimals = getBooleanValue("useDecimals", true);
	protected boolean usePrefixes = true;

	protected BNodeWriter bnodeWriter = null;

	// ----------------------------------------------------
	// Jena RDFWriter interface

	protected N3JenaWriterCommon() {
		bnodeWriter = new BNodeWriter();
	}

	protected N3JenaWriterCommon(BNodeWriter writer) {
		this.bnodeWriter = writer;
	}

	@Override
	public RDFErrorHandler setErrorHandler(RDFErrorHandler errHandler) {
		RDFErrorHandler old = errorHandler;
		errorHandler = errHandler;
		return old;
	}

	@Override
	public Object setProperty(String propName, Object propValue) {
		if (!(propValue instanceof String)) {
//			logger.warn("N3.setProperty: Property for '" + propName + "' is not a string");
			propValue = propValue.toString();
		}

		// Store absolute name of property
		propName = absolutePropName(propName);
		if (writerPropertyMap == null)
			writerPropertyMap = new HashMap<>();
		Object oldValue = writerPropertyMap.get(propName);
		writerPropertyMap.put(propName, propValue);
		return oldValue;
	}

	/**
	 * Write the model out in N3. The writer should be one suitable for UTF-8 which
	 * excludes a PrintWriter or a FileWriter which use default character set.
	 *
	 * Examples:
	 * 
	 * <pre>
	 * try {
	 *      Writer w =  new BufferedWriter(new OutputStreamWriter(output, "UTF-8")) ;
	 *      model.write(w, base) ;
	 *      try { w.flush() ; } catch (IOException ioEx) {...}
	 *  } catch (java.io.UnsupportedEncodingException ex) {} //UTF-8 is required so can't happen
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 * try {
	 *     OutputStream out = new FileOutputStream(file) ;
	 *     Writer w =  new BufferedWriter(new OutputStreamWriter(out, "UTF-8")) ;
	 *     model.write(w, base) ;
	 * }
	 * catch (java.io.UnsupportedEncodingException ex) {}
	 * catch (java.io.FileNotFoundException noFileEx) { ... }
	 * </pre>
	 * 
	 * @see #write(Model,Writer,String)
	 */

	@Override
	public void write(Model baseModel, Writer _out, String base) {
		try {
			write(baseModel, false, _out, base, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write the model out in N3, encoded in in UTF-8
	 * 
	 * @see #write(Model,Writer,String)
	 */

	@Override
	public synchronized void write(Model model, OutputStream output, String base) {
		write(model, model.getNsPrefixMap(), false, output, base, 0);
	}

	protected synchronized void write(Model model, Map<String, String> prefixMap, boolean nested, OutputStream output,
			String base, int initialIndent) {

		// unique per top-level N3 model
		spec = ((N3Model) model).getSpec();

		try {
			if (usePrefixes) {
				this.prefixMap = new HashMap<>(wellKnownPrefixesMap);
				this.prefixMap.putAll(prefixMap);
			}

			Writer w = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
			try {
				write(model, nested, w, base, initialIndent);
				w.flush();

			} catch (IOException ioEx) {
				throw new JenaException(ioEx);
			}
		} catch (java.io.UnsupportedEncodingException ex) {
			System.err.println("Failed to create UTF-8 writer");
		}
	}

	protected void write(Model baseModel, boolean nested, Writer _out, String base, int initialIndent)
			throws IOException {

		this.model = baseModel;

		if (usePrefixes)
			model.getNsPrefixMap().forEach((k, v) -> wellKnownPrefixesMap.put(k, v));

		if (!(_out instanceof BufferedWriter))
			_out = new BufferedWriter(_out);
		out = new N3IndentedWriter(_out);

		out.incIndent(initialIndent);

// BaseURI - <#>        
//        if ( base != null )
//        {
//            baseURIref = base ;
//            if ( !base.endsWith("#") &&! isOpaque(base) )
//                baseURIrefHash = baseURIref+"#" ;
//        }

		processModel(baseModel, nested);
	}

	// ----------------------------------------------------
	// The assumed processing model is:
	// Writing N3 involves ordering the graph into:
	// -- Subjects
	// -- Property lists within subjects
	// -- Object lists with in properties

	// A derived class may choose to intercept and implement at any of these levels.

	// Standard layout is:
	// subject
	// property1 value1 ;
	// property2 value2 ;
	// property3 value3 .

	// Normal hook points for subclasses.

	protected void startWriting() {
	}

	protected void finishWriting() {
	}

	protected void prepare(Model model) {
	}

	protected void processModel(Model model, boolean nested) throws IOException {
		if (usePrefixes) {
			reversePrefixMap.clear();
			for (Iterator<Entry<String, String>> iter = prefixMap.entrySet().iterator(); iter.hasNext();) {
				Entry<String, String> e = iter.next();
				String prefix = e.getKey();
				String uri = e.getValue();

				// XML namespaces name can include '.'
				// Turtle prefixed names can't.
				if (!checkPrefixPart(prefix))
					iter.remove();
				else {
					if (checkPrefixPart(prefix))
						// Build acceptable reverse mapping
						reversePrefixMap.put(uri, prefix);
				}
			}
		}

		startWriting();
		prepare(model);

		writeHeader(model);
		// only write prefixes in outer model
		if (!nested)
			writePrefixes(model);

		if (prefixMap.size() != 0)
			out.println();

		// Do the output.
		writeModel(model, nested);

		// Release intermediate memory - allows reuse of a writer
		finishWriting();
		// bnodeWriter = null;
	}

	protected void writeModel(Model model, boolean nested) throws IOException {
		writePerSubject(model);
	}

	protected void writePerSubject(Model model) throws IOException {
		// Needed only for no prefixes, no blank first line.
		boolean doingFirst = true;
		ResIterator rIter = listSubjects(model);

		for (; rIter.hasNext();) {
			// Subject:
			// First - it is something we will write out as a structure in an object field?
			// That is, a RDF list or the object of exactly one statement.
			Resource subject = rIter.nextResource();

			if (skipThisSubject(subject)) {
				if (N3JenaWriter.DEBUG)
					out.println("# Skipping: " + formatResource(subject));
				continue;
			}

			// We really are going to print something via writeTriples
			if (doingFirst)
				doingFirst = false;
			else
				out.println();

			writeOneGraphNode(subject);

		}
		rIter.close();
	}

	protected ResIterator listSubjects(Model model) {
		return model.listSubjects();
	}

	protected void writeOneGraphNode(Resource subject) throws IOException {
		// New top level item.
		// Does not take effect until newline.
		out.incIndent(indentProperty);

		ClosableIterator<Resource> iter = preparePropertiesForSubject(subject);
		if (iter.hasNext()) {
			writeSubjectAndProperties(subject, iter);

			out.decIndent(indentProperty);
			out.println(" .");
		}
	}

	protected void writeSubjectAndProperties(Resource subj, ClosableIterator<Resource> iter) {
		Resource predicate = iter.next();
		writeSubject(subj, predicate);

		for (;;) {
			writeObjectList(subj, predicate);
			if (iter.hasNext()) {
				out.println(" ;");
				predicate = iter.next();

			} else
				break;
		}

		iter.close();
	}

	protected void writePropertiesForSubject(Resource subj, ClosableIterator<Resource> iter) {
		// For each property.
		for (; iter.hasNext();) {
			Resource predicate = iter.next();

			// Object list
			writeObjectList(subj, predicate);

			if (iter.hasNext())
				out.println(" ;");
		}
		iter.close();
	}

	// Hook called on every resource.
	// Since there is spacing bewteen resource frames, need to know
	// whether an item will cause any output.
	protected boolean skipThisSubject(Resource r) {
		return false;
	}

	// This is the hook called within writeModel.
	// NB May not be at the top level (indent = 0)

	protected void writeSubject(Resource subject) {
		writeSubject(subject, null);
	}

	protected void writeSubject(Resource subject, Resource firstPredicate) {
		String subjStr = formatResource(subject);
		out.print(subjStr);
		// May be very short : if so, stay on this line.

		// Currently at end of subject.
		// NB shortSubject is (subjectColumn-minGap) so there is a gap.

		// some constructs will span multiple lines; get length of last line
		int len = subjStr.length();
		switch (subject.getNodeType()) {

		case CITED_FORMULA:
			len = subjStr.substring(subjStr.lastIndexOf("}") + 1).length();
			break;

		case COLLECTION:
			len = subjStr.substring(subjStr.lastIndexOf(")") + 1).length();
			break;

		default:
			break;
		}

		if (len < shortSubject)
			out.print(pad(2));
		else
			// Does not fit this line.
			out.println();
	}

	protected void writeHeader(Model model) {
// BaseURI - <#>
//        if (baseURIref != null && !baseURIref.equals("") )
//            out.println("# Base: " + baseURIref);
	}

	protected N3IndentedWriter getOutput() {
		return out;
	}

	protected Map<String, String> getPrefixes() {
		return prefixMap;
	}

	protected void writePrefixes(Model model) {
		for (Entry<String, String> entry : prefixMap.entrySet()) {
			String u = entry.getValue();
			String p = entry.getKey();

// BaseURI - <#>            
//            // Special cases: N3 handling of base names.
//            if (doAbbreviatedBaseURIref && p.equals(""))
//            {
//                if (baseURIrefHash != null && u.equals(baseURIrefHash))
//                    u = "#";
//                if (baseURIref != null && u.equals(baseURIref))
//                    u = "";
//            }

			String tmp = "@prefix " + p + ": ";
			out.print(tmp);
			out.print(pad(16 - tmp.length()));
			// NB Starts with a space to ensure a gap.
			out.println(" <" + u + "> .");
		}

		if (model.getBase() != null) {
			out.println("@base <" + model.getBase() + "> .");
		}
	}

	protected void writeObjectList(Resource subject, Resource predicate) {
		curIndent += indentProperty;
		String predStr = formatNode(predicate);

		if (predicate.isURI() && wellKnownPropsMap.containsKey(predicate.getURI()))
			predStr = (String) wellKnownPropsMap.get(predicate.getURI());

		// Write with object lists as clusters of statements with the same property
		// Looks more like a machine did it but fewer bad cases.

		curIndent += indentObject;
		curPredicate = predicate;

		StmtIterator sIter = subject.listProperties(predicate.asResource());

		for (; sIter.hasNext();) {
			Statement stmt = sIter.nextStatement();

			String objStr = formatNode(stmt.getObject());
			writePredicateObject(predStr, objStr);

			if (sIter.hasNext()) {
				out.println(" ;");
			}
		}

		curPredicate = null;
		curIndent -= indentObject;

		sIter.close();
		curIndent -= indentProperty;
	}

	protected void writePredicateObject(Resource pred, Resource obj) {
		curIndent += indentProperty;
		String predStr = formatNode(pred);

		curIndent += indentObject;
		curPredicate = pred;
		String objStr = formatNode(obj);

		writePredicateObject(predStr, objStr);

		curPredicate = null;
		curIndent -= indentObject;
		curIndent -= indentProperty;
	}

	protected void writePredicateObject(String predStr, String objStr) {
		out.print(predStr);
		out.incIndent(indentObject);

		if ((predStr.length() + minGap) <= widePropertyLen) {
			// Property col allows for min gap but widePropertyLen > propertyCol
			// (which looses alignment - this is intentional.
			// Ensure there is at least min gap.

			int padding = calcPropertyPadding(predStr);
			out.print(pad(padding));

//            if ( propStr.length() < propertyWidth ) 
//                out.print( pad(propertyCol-minGap-propStr.length()) ) ;
//            out.print(minGapStr) ;
		} else
			// Does not fit this line.
			out.println();

		// Write one object - simple writing.

		out.print(objStr);
		out.decIndent(indentObject);
	}

	protected void writeObject(Resource node) {
		if (node instanceof Literal) {
			writeLiteral((Literal) node);
			return;
		}

		Resource rObj = (Resource) node;

		out.print(formatResource(rObj));
	}

	protected void writeLiteral(Literal literal) {
		out.print(formatResource(literal));
	}

	protected ClosableIterator<Resource> preparePropertiesForSubject(Resource r) {
		// Properties to do.
		Set<Resource> properties = new HashSet<>();

//		Log.info(getClass(), "listing properties: " + r);
		StmtIterator sIter = r.listProperties();
		for (; sIter.hasNext();) {
			Statement stmt = sIter.nextStatement();
//			Log.info(getClass(), "stmt? " + stmt);
			properties.add(stmt.getPredicate());
		}
		sIter.close();
		// Log.info(getClass(), "properties? " + properties);
		return WrappedIterator.create(properties.iterator());
	}

	public String formatNode(RDFObject node) {
		return formatResource(node.asResource());
	}

	// Utility operations
	public String formatResource(Resource r) {
		Model m = r.getModel();

		switch (r.getNodeType()) {

		case CITED_FORMULA:
			return formatCitedFormula((Node_CitedFormula) r.asNode(), m);

		case QUICK_VAR:
			return formatVariable((Node_QuickVariable) r.asNode(), m);

		case QUANT_VAR:
			return formatQuantifiedVariable((Node_QuantifiedVariable) r.asNode(), m);

		case COLLECTION:
			return formatCollection((Node_Collection) r.asNode(), m);

		case LITERAL:
			return formatLiteral((Node_Literal) r.asNode(), m);

		case BLANK:
			return formatBlankNode(r);

		case URI:
			if (r.equals(RDF.nil))
				return "()";
			return formatURI(r.getURI());

		default:
			return null;
		}
	}

	public String formatNode(Node n, Model m) {
		switch (n.getType()) {

		case CITED_FORMULA:
			return formatCitedFormula((Node_CitedFormula) n, m);

		case QUICK_VAR:
			return formatVariable((Node_QuickVariable) n, m);

		case QUANT_VAR:
			return formatQuantifiedVariable((Node_QuantifiedVariable) n, m);

		case COLLECTION:
			return formatCollection((Node_Collection) n, m);

		case LITERAL:
			return formatLiteral((Node_Literal) n, m);

		case BLANK:
			return formatBlankNode(m.createResource(new AnonId(n.getBlankNodeId())));

		case URI:
			if (n.equals(RDF.nil.asNode()))
				return "()";
			return formatURI(n.getURI());

		// shouldn't occur when printing the model
		case RULEVAR:
			return formatVariable((Node_Variable) n, m);

		default:
			return null;
		}
	}

	protected String formatCitedFormula(Node_CitedFormula node, Model m) {
		N3Model model = node.getContents();

		StringBuffer str = new StringBuffer();
		str.append("{");

		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		// TODO better way to do this
		// (i.e., directly print statements to "out" instead
		// of buffering in string)

		// this assumes that cited formulas won't be too large

		N3JenaWriterFull subWriter = (N3JenaWriterFull) copy();

		int baseIndent = curIndent;
		// want to print implication heads on same indent as body
		if (curPredicate != null && ignorePaddingPropsN3.contains(curPredicate))
			baseIndent -= indentProperty + indentObject;

		int nestedIndent = baseIndent + indentProperty;

		subWriter.write(model, prefixMap, true, bao, "", nestedIndent);

		String contents = new String(bao.toByteArray());

		// ignore any prepared whitespace after last newline
		str.append(contents, 0, contents.lastIndexOf("\n"));
		// (instead, append our own indent at subject-padding)
//		str.append("}");
		str.append(pad(baseIndent)).append("}");

		return str.toString();
	}

	private String formatVariable(Node_Variable node, Model m) {
		return "?" + node.getName();
	}

	private String formatQuantifiedVariable(Node_QuantifiedVariable node, Model m) {
		return formatURI(node.getName());
	}

	private String formatCollection(Node_Collection coll, Model m) {
		StringBuffer str = new StringBuffer();

		List<Node> elements = coll.getElements();
		for (Node el : elements) {
			str.append(formatNode(el, m)).append(" ");
		}

		String ret = str.toString().trim();

		return "( " + ret + " )";
	}

	protected String formatQuantifier(Quantifier q) {
		return formatQuantifier((Node_Quantifier) q.asNode(), q.getModel());
	}

	protected String formatQuantifier(Node_Quantifier q, Model m) {
		StringBuffer vars = new StringBuffer();

		Iterator<Node_QuantifiedVariable> varIt = q.getVariables();
		vars.append(Streams.stream(varIt).filter(v -> !v.isQuickVariable())
				.map(n -> formatURI(((QuantifiedVariable) n).getName())).collect(Collectors.joining(", ")));

		if (vars.length() == 0)
			return null;

		StringBuffer ret = new StringBuffer();
		switch (q.getQuantifierType()) {

		case UNIVERSAL:
			ret.append("@forAll ");
			break;

		case EXISTENTIAL:
			ret.append("@forSome ");
			break;
		}

		ret.append(vars).append(" .");

		return ret.toString();
	}

	protected String formatLiteral(Node_Literal literal, Model m) {
		String datatype = literal.getLiteralDatatypeURI();
		String lang = literal.getLiteralLanguage();
		String s = literal.getLiteralLexicalForm();

//		if (datatype != null) {
//			// Special form we know how to handle?
//			// Assume valid text
//			if (datatype.equals(XSD.integer.getURI())) {
//				try {
//					new java.math.BigInteger(s);
//					return s;
//				} catch (NumberFormatException nfe) {
//				}
//				// No luck. Continue.
//				// Continuing is always safe.
//			}
//
//			if (datatype.equals(XSD.decimal.getURI())) {
//				// Must have ., can't have e or E
//				if (s.indexOf('.') >= 0 && s.indexOf('e') == -1 && s.indexOf('E') == -1) {
//					// Turtle - N3 does not allow .3 +.3 or -.3
//					// See if parsable.
//					try {
//						new BigDecimal(s);
//						return s;
//					} catch (NumberFormatException nfe) {
//					}
//				}
//			}
//
//			if (this.allowDoubles && datatype.equals(XSD.xdouble.getURI())) {
//				// Must have 'e' or 'E' (N3 and Turtle now read 2.3 as a decimal).
//				if (s.indexOf('e') >= 0 || s.indexOf('E') >= 0) {
//					try {
//						// Validate it.
//						Double.parseDouble(s);
//						return s;
//					} catch (NumberFormatException nfe) {
//					}
//					// No luck. Continue.
//				}
//			}
//		}

		// Format the text - with escaping.
		StringBuffer sbuff = new StringBuffer();
		boolean singleQuoteLiteral = true;

		String quoteMarks = "\"";

		// Things that force the use of """ strings
		if (this.allowTripleQuotedStrings
				&& (s.indexOf("\n") != -1 || s.indexOf("\r") != -1 || s.indexOf("\f") != -1)) {
			quoteMarks = "\"\"\"";
			singleQuoteLiteral = false;
		}

		sbuff.append(quoteMarks);
		string(sbuff, s, singleQuoteLiteral);
		sbuff.append(quoteMarks);

		if (TermUtil.isLangString(literal)) {
			sbuff.append("@");
			sbuff.append(lang);
			// TODO
		} else if (!TermUtil.isSimpleString(literal)) {
			sbuff.append("^^");
			sbuff.append(formatURI(datatype));
		}
		return sbuff.toString();
	}

	protected String formatProperty(Resource p) {
		String prop = p.getURI();
		if (this.useWellKnownPropertySymbols && wellKnownPropsMap.containsKey(prop))
			return wellKnownPropsMap.get(prop);

		return formatURI(prop);
	}

	protected String formatBlankNode(Resource r) {
		if (!alwaysAllocateBNodeLabel) {
			// Does anything point to it?
			StmtIterator sIter = r.getModel().listStatements(null, null, r);

			if (!sIter.hasNext()) {
				sIter.close();
				// This bNode is not referenced so don't need the bNode Id.
				// Must be a subject - indent better be zero!
				// This only happens for subjects because object bNodes
				// referred to once (the other case for [] syntax)
				// are handled elsewhere (by oneRef set)

				// Later: use [ prop value ] for this.
				return "[]";
			}
			sIter.close();
		}

		return bnodeWriter.prettyPrint(r.getId());

		// (debugging) return internal bnode id
//		return "_:" + r.getId().toString();
	}

	protected String formatURI(String uriStr) {

// BaseURI - <#>		
//        if ( doAbbreviatedBaseURIref && uriStr.equals(baseURIref) )
//            return "<>" ;

		// Try for a prefix and write as prefixed name.
		// 1/ Try splitting as a prefixed name
		// 2/ Search for possibilities

		// Stage 1.
		int idx = splitIdx(uriStr);
		// Depends on legal URIs.
		if (idx >= 0) {
			// Include the # itself.
			String x = uriStr.substring(0, idx + 1);

			// format skolem URIs so we can reverse-engineer them to the original bnode
//			if (x.startsWith(N3Skolem.uri)) {

			// TODO already written at this point ..
//				this.prefixMap.put("sk", spec.getSkolemNs());
//
//				String localPart = uriStr.substring(idx + 1);
//				String id = bnodeWriter.prettyPrint(localPart);
//
//				return "sk:" + id;
//			}

			String prefix = reversePrefixMap.get(x);
			if (prefix != null) {
				String localPart = uriStr.substring(idx + 1);
				if (checkNamePart(localPart))
					return prefix + ':' + localPart;
			}
		}

		// Unsplit. Could just return here.
//		// Find the longest if several.
//        // Possible optimization: split URI and have URI=> ns: map.
//        // Ordering prefixes by length, then first hit is better.
//        // 
//        // Also: could just assume that the split is on / or #
//        // Means we need to find a prefix just once. 
//		for ( Iterator<String> pIter = prefixMap.keySet().iterator() ; pIter.hasNext() ; )
//		{
//			String p = pIter.next() ;
//			String u = prefixMap.get(p) ;
//			if ( uriStr.startsWith(u) )
//				if ( matchURI.length() < u.length() )
//				{
//					matchPrefix = p ;
//					matchURI = u ;
//				}
//		}
//		if ( matchPrefix != null )
//		{
//			String localname = uriStr.substring(matchURI.length()) ;
//            
//            if ( checkPrefixedName(matchPrefix, localname) )
//                return matchPrefix+":"+localname ;
//
//            // Continue and return quoted URIref
//		}

		// Not as a prefixed name - write as a quoted URIref
		// It should be right - the writer should be UTF-8 on output.
		return "<" + uriStr + ">";
	}

	protected static int splitIdx(String uriStr) {
		int idx = uriStr.lastIndexOf('#');
		if (idx >= 0)
			return idx;
		// No # - try for /
		idx = uriStr.lastIndexOf('/');
		return idx;
	}

	// Checks of prefixed names
	// These tests must agree, or be more restrictive, than the parser.
	protected static boolean checkPrefixedName(String ns, String local) {
		return checkPrefixPart(ns) && checkNamePart(local);
	}

	/*
	 * http://www.w3.org/TeamSubmission/turtle/#sec-grammar-grammar [27] qname ::=
	 * prefixName? ':' name? [30] nameStartChar ::= [A-Z] | "_" | [a-z] |
	 * [#x00C0-#x00D6] | [#x00D8-#x00F6] | [#x00F8-#x02FF] | [#x0370-#x037D] |
	 * [#x037F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] |
	 * [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF] [31]
	 * nameChar ::= nameStartChar | '-' | [0-9] | #x00B7 | [#x0300-#x036F] |
	 * [#x203F-#x2040] [32] name ::= nameStartChar nameChar* [33] prefixName ::= (
	 * nameStartChar - '_' ) nameChar*
	 */

	protected static boolean checkPrefixPart(String s) {
		if (s.length() == 0)
			return true;
		CharacterIterator cIter = new StringCharacterIterator(s);
		char ch = cIter.first();
		if (!checkNameStartChar(ch))
			return false;
		if (ch == '_') // Can't start with _ (bnodes labels handled separately)
			return false;
		return checkNameTail(cIter);
	}

	protected static boolean checkNamePart(String s) {
		if (s.length() == 0)
			return true;
		CharacterIterator cIter = new StringCharacterIterator(s);
		char ch = cIter.first();
		if (!checkNameStartChar(ch))
			return false;
		return checkNameTail(cIter);
	}

	private static boolean checkNameTail(CharacterIterator cIter) {
		// Assumes cIter.first already called but nothing else.
		// Skip first char.
		char ch = cIter.next();
		for (; ch != java.text.CharacterIterator.DONE; ch = cIter.next()) {
			if (!checkNameChar(ch))
				return false;
		}
		return true;
	}

	protected static boolean checkNameStartChar(char ch) {
		if (Character.isLetter(ch))
			return true;
		if (ch == '_')
			return true;
		return false;
	}

	protected static boolean checkNameChar(char ch) {
		if (Character.isLetterOrDigit(ch))
			return true;
		if (ch == '_')
			return true;
		if (ch == '-')
			return true;
		return false;
	}

	protected final static String WS = "\n\r\t";

	protected static void string(StringBuffer sbuff, String s, boolean singleQuoteLiteral) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			// Escape quotes
			if (c == '"') {
				sbuff.append('\\');
				sbuff.append(c);
				continue;
			}

			// Characters to literally output.
			// This would generate 7-bit safe files
//            if (c >= 32 && c < 127)
//            {
//                sbuff.append(c) ;
//                continue;
//            }    

			// Whitespace
			if (singleQuoteLiteral && (c == '\n' || c == '\r' || c == '\f')) {
				if (c == '\n')
					sbuff.append("\\n");
				if (c == '\t')
					sbuff.append("\\t");
				if (c == '\r')
					sbuff.append("\\r");
				if (c == '\f')
					sbuff.append("\\f");
				continue;
			}

			// Output as is (subject to UTF-8 encoding on output that is)
			sbuff.append(c);

//            // Unicode escapes
//            // c < 32, c >= 127, not whitespace or other specials
//            String hexstr = Integer.toHexString(c).toUpperCase();
//            int pad = 4 - hexstr.length();
//            sbuff.append("\\u");
//            for (; pad > 0; pad--)
//                sbuff.append("0");
//            sbuff.append(hexstr);
		}
	}

	protected int calcPropertyPadding(String propStr) {
		int padding = propertyCol - propStr.length();
		if (padding < minGap)
			padding = minGap;
		return padding;
	}

	protected static String pad(int cols) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cols; i++)
			sb.append(' ');
		return sb.toString();
	}

	// Utilities

	protected int countProperties(Resource r) {
		int numProp = 0;
		StmtIterator sIter = r.listProperties();
		for (; sIter.hasNext();) {
			sIter.nextStatement();
			numProp++;
		}
		sIter.close();
		return numProp;
	}

	protected int countProperties(Resource r, Property p) {
		int numProp = 0;
		StmtIterator sIter = r.listProperties(p);
		for (; sIter.hasNext();) {
			sIter.nextStatement();
			numProp++;
		}
		sIter.close();
		return numProp;
	}

	protected int countArcsTo(Resource resource) {
		return countArcsTo(null, resource);
	}

	protected int countArcsTo(Property prop, Resource resource) {
		int numArcs = 0;
		StmtIterator sIter = resource.getModel().listStatements(null, prop, resource);
		for (; sIter.hasNext();) {
			sIter.nextStatement();
			numArcs++;
		}
		sIter.close();
		return numArcs;
	}

	protected Iterator<Resource> rdfListIterator(Resource r) {
		List<Resource> list = new ArrayList<>();

		for (; !r.equals(RDF.nil);) {
			StmtIterator sIter = r.getModel().listStatements(r, RDF.first, (Resource) null);
			list.add(sIter.nextStatement().getObject());
			if (sIter.hasNext())
				// @@ need to cope with this (unusual) case
				throw new JenaException("N3: Multi valued list item");
			sIter = r.getModel().listStatements(r, RDF.rest, (Resource) null);
			r = (Resource) sIter.nextStatement().getObject();
			if (sIter.hasNext())
				throw new JenaException("N3: List has two tails");
		}
		return list.iterator();
	}

	// Convenience operations for accessing system properties.

	protected String getStringValue(String prop, String defaultValue) {
		String p = getPropValue(prop);

		if (p == null)
			return defaultValue;
		return p;
	}

	protected boolean getBooleanValue(String prop, boolean defaultValue) {
		String p = getPropValue(prop);

		if (p == null)
			return defaultValue;

		if (p.equalsIgnoreCase("true"))
			return true;

		if (p.equals("1"))
			return true;

		return false;
	}

	protected int getIntValue(String prop, int defaultValue) {
		String p = getPropValue(prop);
		if (p == null)
			return defaultValue;
		try {
			return Integer.parseInt(p);
		} catch (NumberFormatException ex) {
			logger.warn("Format error for property: " + prop);
			return defaultValue;
		}
	}

	// May be the absolute or local form of the property name

	protected String getPropValue(String prop) {
		prop = absolutePropName(prop);
		if (writerPropertyMap != null && writerPropertyMap.containsKey(prop)) {
			Object obj = writerPropertyMap.get(prop);
			if (!(obj instanceof String))
				logger.warn("getPropValue: N3 Property for '" + prop + "' is not a string");
			return (String) obj;
		}
		String s = JenaRuntime.getSystemProperty(prop);
		if (s == null)
			s = JenaRuntime.getSystemProperty(localPropName(prop));
		return s;
	}

	protected String absolutePropName(String propName) {
		if (propName.indexOf(':') == -1)
			return N3JenaWriter.propBase + propName;
		return propName;
	}

	protected String localPropName(String propName) {
		if (propName.startsWith(N3JenaWriter.propBase))
			propName = propName.substring(N3JenaWriter.propBase.length());
		return propName;
	}

	protected N3JenaWriterCommon copy() {
		return new N3JenaWriterCommon(bnodeWriter);
	}

//	private boolean isOpaque(String uri) {
//		try {
//			return new URI(uri).isOpaque();
//		} catch (URISyntaxException ex) {
//			return true;
//		}
//	}

	protected class BNodeWriter {

		protected Map<String, String> bNodesMap = new HashMap<>(); // BNodes seen.
		protected int bNodeCounter = 0;

		public String prettyPrint(String bnodeId) {
			if (!bNodesMap.containsKey(bnodeId))
				bNodesMap.put(bnodeId, "b" + (++bNodeCounter));

			return bNodesMap.get(bnodeId);
		}

		public String prettyPrint(AnonId id) {
			return "_:" + prettyPrint(id.getLabelString());
		}
	}
}
