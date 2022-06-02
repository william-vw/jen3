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

package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_MISUSE_NS;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_PRED_RESOURCE_PATH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.n3.turtle.LabelToNodeMap;
import org.apache.jen3.n3.turtle.ParserBase;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.QuickVariable;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.shared.JenaException;
import org.apache.jen3.vocabulary.N3;
import org.apache.jen3.vocabulary.N3Log;
import org.apache.jen3.vocabulary.OWL;
import org.apache.jen3.vocabulary.RDF;

import w3c.n3dev.parser.n3ParserErrorListener;
import w3c.n3dev.parser.antlr.n3Parser;
import w3c.n3dev.parser.antlr.n3Parser.BlankNodeContext;
import w3c.n3dev.parser.antlr.n3Parser.CollectionContext;
import w3c.n3dev.parser.antlr.n3Parser.FormulaContext;
import w3c.n3dev.parser.antlr.n3Parser.NumericLiteralContext;
import w3c.n3dev.parser.antlr.n3Parser.PathContext;
import w3c.n3dev.parser.antlr.n3Parser.PathItemContext;
import w3c.n3dev.parser.antlr.n3Parser.PredicateContext;
import w3c.n3dev.parser.antlr.n3Parser.QuickVarContext;
import w3c.n3dev.parser.antlr.n3Parser.TriplesContext;
import w3c.n3dev.parser.event.n3EventHandler;

public class N3GraphInserter extends n3EventHandler {

	private static final int IRI = 0;
	private static final int BNODE = 1;
	private static final int LITERAL = 2;
	private static final int FORMULA = 3;
	private static final int QUICKVAR = 4;
	private static final int COLLECTION = 5;

	private BuiltinSet builtinSet;

	private ParserBase utils = new ParserBase();

	private LinkedList<State> stack = new LinkedList<>();
	private State state;

	public N3GraphInserter(String baseUri, Model model, n3ParserErrorListener errorListener) {
		super(baseUri, errorListener);

		if (!(model instanceof N3Model))
			throw new JenaException(
					"Require an N3 model to read N3 data. Please use ModelFactory.createN3Model to create an N3 model.");

		N3Model n3Model = (N3Model) model;

		this.spec = n3Model.getSpec();
		this.builtinSet = spec.getBuiltinSet();

		this.state = new State(n3Model);

		if (this.docUri != null) {
			// per default, set empty NS prefix to document URI
			setPrefix("", this.docUri.toString());
			// per default, set base URI to document URI
			setBase(this.docUri.toString());
		}
	}

	private void subFormula(CitedFormula formula) {
		stack.add(state);

		state = state.subState(formula);
	}

	private void subState() {
		stack.add(state);

		state = state.subState();
	}

	private void popState() {
		// if (!stack.isEmpty())
		state = stack.removeLast();
	}

	private State peekState() {
		return stack.getLast();
	}

	private void emitTriple() {
		emitTriple(state.triple);
	}

	private void emitTriple(Node[] triple) {
		if (state.invPred)
			triple = new Node[] { triple[2], triple[1], triple[0] };

		state.add(new Triple(triple[0], triple[1], triple[2]));
	}

	@Override
	protected void setBase(String uri) {
		state.setBase(uri);
	}

	@Override
	protected void setPrefix(String prefix, String uri) {
		state.setPrefix(prefix, uri);
	}

	@Override
	protected String getPrefix(String prefix) {
		return state.resolvePrefix(prefix);
	}

	@Override
	public void exitSubject(n3Parser.SubjectContext ctx) {
		state.triple[0] = state.path;
	}

	@Override
	public void exitVerb(n3Parser.VerbContext ctx) {
		Node predicate = null;

		if (ctx.predicate() != null)
			predicate = state.consumePredicate();
		else {
			String token = text(ctx.getStart());
			if (token.equals("a"))
				predicate = RDF.type.asNode();

			else if (token.equals("has"))
				predicate = state.path;

			else if (token.equals("is")) {
				state.invPred = true;
				predicate = state.path;

			} else if (token.equals("="))
				predicate = OWL.sameAs.asNode();

			else if (token.equals("=>"))
				predicate = N3Log.implies.asNode();

			else if (token.equals("<=")) {
				predicate = N3Log.impliedBy.asNode();
			}
		}

		state.triple[1] = predicate;
	}

	@Override
	public void exitPredicate(n3Parser.PredicateContext ctx) {
		// or, check for token with type "X" ..
		state.invPred = text(ctx.getStart()).equals("<-");

		state.predicate = state.path;
	}

	@Override
	public void exitTriples(TriplesContext ctx) {
		state.invPred = false;
	}

	@Override
	public void exitObject(n3Parser.ObjectContext ctx) {
		if (!state.collecting()) {
			state.triple[2] = state.path;
			emitTriple();
		}
	}

	@Override
	public void enterPath(PathContext ctx) {
		state.pathCnt++;
		if (state.pathCnt > 1) {
			// in case of parse error
			if (state.pathItem == null)
				return;

			// after first & before second path-item
			// (we're in "enter" method)
			if (state.pathCnt == 2) {
				// current path-item = first item
				state.pathStart = state.pathItem;

				if (spec.hasFeedbackFor(SYNTAX_PRED_RESOURCE_PATH)) {
					RuleContext spoCtx = ctx.parent.parent.parent;

					if (spoCtx instanceof PredicateContext)
						spec.getFeedback(SYNTAX_PRED_RESOURCE_PATH).doDefaultAction();
				}

			} else {
				// after second path-item

				// emit intermediary path triple
				// set next path's start as prior path's end
				state.pathStart = emitPath();
			}

			// parent = prior path call
			// child at idx 1 will always be either "!" or "^"
			state.invPath = text(ctx.parent.getChild(1)).equals("^");
		}
	}

	@Override
	public void exitPath(PathContext ctx) {
		state.pathCnt--;
		// exiting (nested) path calls
		if (state.pathCnt == 0) {
			// in case of parse error
			if (state.pathItem == null)
				return;

			// in case nesting took place
			if (state.pathStart != null) {
				// emit last path triple

				// set last path's end as path object
				// to be consumed by subject, predicate or object, depending on s/p/o
				state.setPath(emitPath());

				state.pathStart = null;

			} else
				// set single path-item as path object
				state.setPath(state.pathItem);
		}
	}

	private Node emitPath() {
		// pathStart = first path-item or object from prior path triple
		// pathItem = second, third, .. path item

		Node pathStart = state.pathStart;
		Node pathPred = state.pathItem;
		Node pathEnd = state.createBNode();

		if (state.invPath)
			emitTriple(new Node[] { pathEnd, pathPred, pathStart });
		else
			emitTriple(new Node[] { pathStart, pathPred, pathEnd });

		// always continue nav from new bnode
		return pathEnd;
	}

	@Override
	public void exitPathItem(PathItemContext ctx) {
		state.pathItem = state.consumePathItem();
	}

	@Override
	public void exitQuickVar(QuickVarContext ctx) {
		String name = varName(ctx.QuickVarName());
		Node var = state.resolveQuickVar(name);

		state.setQuickvar(var);
	}

	private Node_QuickVariable quickQuantifier(String name) {
		QuickVariable v = state.model.createQuickVariable(name);

		Node_QuickVariable qn = (Node_QuickVariable) v.asNode();
//		state.newExplicitVar(qn);

		return qn;
	}

	@Override
	public void enterBlankNodePropertyList(n3Parser.BlankNodePropertyListContext ctx) {
		subState();

		// assign bnode as subject to all predicate-objects in the list
		state.triple[0] = state.createBNode();
	}

	@Override
	public void exitBlankNodePropertyList(n3Parser.BlankNodePropertyListContext ctx) {
		Node prevIri = state.triple[0];

		popState();
		// assign the bnode subject to iri
		// to be consumed by subject, predicate or object, depending on s/p/o
		state.setIri(prevIri);
	}

	@Override
	public void enterFormula(FormulaContext ctx) {
		N3ModelSpec spec = N3ModelSpec.get(Types.N3_MEM);

		subFormula(state.model.createCitedFormula(spec));
	}

	@Override
	public void exitFormula(FormulaContext ctx) {
		CitedFormula prevFormula = peekState().currentFormula;
		prevFormula.close();

		popState();

		// to be consumed by subject, predicate or object, depending on s/p/o
		state.setFormula(prevFormula.asNode());
	}

	@Override
	public void enterCollection(CollectionContext ctx) {
		subState();

		state.startCollection();
	}

	@Override
	public void exitCollection(CollectionContext ctx) {
		List<Node> elements = state.endCollection();

		popState();
		// to be consumed by subject, predicate or object, depending on s/p/o
		state.setCollection(NodeFactory.createCollection(elements));
	}

	@Override
	public void exitIri(n3Parser.IriContext ctx) {
		TerminalNode iriRef = ctx.IRIREF();

		if (iriRef != null)
			setIri(iri(iriRef));
	}

	@Override
	protected void setIri(String iri) {
		state.setIri(iri);
	}

	@Override
	public void exitBlankNode(BlankNodeContext ctx) {
		if (ctx.BLANK_NODE_LABEL() != null)
			state.setBNode(state.createBNode(text(ctx.BLANK_NODE_LABEL())));
		else
			state.setBNode(state.createBNode());
	}

	@Override
	public void exitLiteral(n3Parser.LiteralContext ctx) {
		if (state.literal != null)
			return;

		TerminalNode b = ctx.BooleanLiteral();
		if (b != null) {
			String lex = bool(b);
			state.setLiteral(state.model.createLiteral(lex, XSDDatatype.XSDboolean).asNode());
		}
	}

	// we deal with tokens in two ways:

	// - in exitNumericLiteral, we redefined the lexer rule as a parser rule
	// that way, we can ask the context which sub-types (int, double, ..) was input
	// since it would be a bit of a hassle to reverse-engineer that type

	// - in exitRdfLiteral (String token), we leave the String lexer rule as-is
	// and simply remove the quotes (1x or 3x) as needed since that's quite easy

	@Override
	public void exitNumericLiteral(NumericLiteralContext ctx) {
		TerminalNode n = null;
		XSDDatatype d = null;

		if (ctx.INTEGER() != null) {
			n = ctx.INTEGER();
			d = XSDDatatype.XSDint;

		} else if (ctx.DOUBLE() != null) {
			n = ctx.DOUBLE();
			d = XSDDatatype.XSDdouble;

		} else if (ctx.DECIMAL() != null) {
			n = ctx.DECIMAL();
			d = XSDDatatype.XSDdecimal;
		}

		String lex = text(n);
		state.setLiteral(state.model.createLiteral(lex, d).asNode());
	}

	@Override
	public void exitRdfLiteral(n3Parser.RdfLiteralContext ctx) {
		String lex = string(ctx.String());

		String lang = text(ctx.LANGTAG());
		String datatype = (state.iri != null ? state.consumeIri().getURI() : null);

		state.setLiteral(utils.createLiteral(lex, lang, datatype));
	}

//	@Override
//	public void enterIriList(IriListContext ctx) {
//		state.startIriList();
//	}
//
//	@Override
//	public void exitIriList(IriListContext ctx) {
//		state.endIriList();
//	}

//	@Override
//	public void enterExistential(ExistentialContext ctx) {
//		state.startQuantifying(Quantifiers.EXISTENTIAL);
//	}
//
//	@Override
//	public void exitExistential(ExistentialContext ctx) {
//		state.endQuantifying();
//	}
//
//	@Override
//	public void enterUniversal(UniversalContext ctx) {
//		state.startQuantifying(Quantifiers.UNIVERSAL);
//	}
//
//	@Override
//	public void exitUniversal(UniversalContext ctx) {
//		state.endQuantifying();
//	}

	private String bool(TerminalNode bool) {
		String text = text(bool);
		if (text.startsWith("@"))
			text = text.substring(1);

		return text;
	}

	private String string(TerminalNode strRef) {
		if (strRef == null)
			return "";

		String text = text(strRef);
		if (text.startsWith("\"\"\""))
			return utils.stripQuotes3(text);
		else
			return utils.stripQuotes(text);
	}

	private String varName(TerminalNode quickVar) {
		String text = text(quickVar);
		if (text == null)
			return null;
		return text.substring(1);
	}

	private class State {

		protected State parent;
		// this really needs to be N3Model, and not its graph
		// else, rules won't be added to its internal rulebase
		protected N3Model model;

		protected Map<String, Node> quickVars = new HashMap<>();
//		protected Map<String, Node> explicitVars = new HashMap<>();

		protected Node[] triple = new Node[3];

		protected boolean invPred = false;

		protected int pathCnt = 0;
		protected Node pathStart = null;
		protected boolean invPath = false;

		protected CitedFormula currentFormula;
		// iris from current list
//		protected Quantifier currentQuantifier;
//		protected List<Node> iris = null;
		// elements from current collection
		protected List<Node> elements = null;

		protected Node predicate = null;
		protected Node path = null;
		protected Node pathItem = null;

		protected int itemType = -1;
		protected Node iri = null;
		protected Node bnode = null;
		protected Node literal = null;
		protected Node quickVar = null;
		protected Node formula = null;
		protected Node collection = null;

		protected LabelToNodeMap bnodes;

		protected State(N3Model model) {
			this.model = model;
			this.bnodes = new LabelToNodeMap();
		}

		protected State(State parent, N3Model model, LabelToNodeMap bnodes) {
			this.parent = parent;
			this.model = model;
			this.bnodes = bnodes;
		}

		public State subState(CitedFormula formula) {
			this.currentFormula = formula;

			return new State(this, formula.open(), new LabelToNodeMap());
		}

		public State subState() {
			return new State(state, state.model, state.bnodes);
		}

		protected Node createBNode() {
			return bnodes.allocNode(model.getScope());
		}

		protected Node createBNode(String label) {
			return bnodes.asNode(label, model.getScope());
		}

		protected void add(Triple t) {
//			System.out.println("emit: " + t);
			model.add(model.createStatement(t));
		}

		protected void startCollection() {
			elements = new ArrayList<>();
		}

		protected boolean collecting() {
			return elements != null;
		}

		protected void collect(Node element) {
			elements.add(element);
		}

		protected List<Node> endCollection() {
			List<Node> ret = elements;
			elements = null;

			return ret;
		}

//		protected void startIriList() {
//			iris = new ArrayList<>();
//		}

//		protected boolean quantifying() {
//			return currentQuantifier != null;
//		}

//		protected boolean listingIris() {
//			return iris != null;
//		}
//
//		protected void list(Node iri) {
//			iris.add(iri);
//		}
//
//		protected List<Node> endIriList() {
//			List<Node> ret = iris;
//			iris = null;
//
//			return ret;
//		}

		// "consume" pattern (i.e., return node and then set to null)
		// is used when multiple options exist (e.g., for pathItem) and
		// we need to select the appropriate choice, i.e., the non-null one
		// null setting is needed to avoid confusing the next choice

		// for pathItem, we additionally set an itemType to avoid half a dozen if's

		protected Node consumePredicate() {
			Node ret = predicate;
			predicate = null;

			return ret;
		}

		protected void setIri(Node iri) {
			this.iri = iri;
			itemType = IRI;
		}

		protected void setIri(String iri) {
			Node n = resolveIri(iri);
			if (spec.hasFeedbackFor(BUILTIN_MISUSE_NS)) {

				if (iri.startsWith(N3.root) && !builtinSet.isBuiltinTerm(n))
					listener.namespaceError(iri, spec.getFeedback(BUILTIN_MISUSE_NS));
			}

//			if (listingIris())
//				list(n);
//			else {
			this.iri = n;
			itemType = IRI;
//			}
		}

//		protected Node newExplicitVar(String iri, Quantifier q) {
//			Node v = model.createQuantifiedVariable(iri, q).asNode();
//			newExplicitVar(v);
//
//			return v;
//		}

//		protected void newExplicitVar(Node v) {
//			explicitVars.put(v.getName(), v);
//		}

		protected void newQuickVar(Node v) {
			quickVars.put(v.getName(), v);
		}

		protected Node resolveIri(String iri) {
//			if (quantifying())
//				return newExplicitVar(iri, currentQuantifier);

//			if (explicitVars.containsKey(iri))
//				return explicitVars.get(iri);

			if (parent != null)
				return parent.resolveIri(iri);

			return state.model.createResource(iri).asNode();
		}

		protected void setBase(String uri) {
			model.setBase(uri);
		}

		protected void setPrefix(String prefix, String uri) {
			model.setNsPrefix(prefix, uri);
		}

		protected String getPrefix(String prefix) {
			return model.getNsPrefixURI(prefix);
		}

		protected boolean hasPrefix(String prefix) {
			return model.getNsPrefixURI(prefix) != null;
		}

		protected String resolvePrefix(String prefix) {
			if (hasPrefix(prefix))
				return getPrefix(prefix);
			else if (parent != null)
				return parent.resolvePrefix(prefix);
			else
				return null;
		}

		protected Node resolveQuickVar(String name) {
			if (quickVars.containsKey(name))
				return quickVars.get(name);

			if (parent != null)
				return parent.resolveQuickVar(name);

			Node_QuickVariable ret = quickQuantifier(name);
			newQuickVar(ret);

			return ret;
		}

		protected void setBNode(Node bnode) {
			this.bnode = bnode;
			itemType = BNODE;
		}

		protected void setLiteral(Node literal) {
			this.literal = literal;
			itemType = LITERAL;
		}

		protected void setFormula(Node formula) {
			this.formula = formula;
			itemType = FORMULA;
		}

		protected void setQuickvar(Node quickVar) {
			this.quickVar = quickVar;
			itemType = QUICKVAR;
		}

		protected void setCollection(Node collection) {
			this.collection = collection;
			itemType = COLLECTION;
		}

		protected void setPath(Node path) {
			if (collecting())
				collect(path);
			else
				this.path = path;
		}

		protected Node consumeIri() {
			Node ret = iri;

			iri = null;
			itemType = -1;

			return ret;
		}

		protected Node consumePathItem() {
			Node ret = null;
			switch (itemType) {
			case IRI:
				ret = iri;
				iri = null;
				break;

			case BNODE:
				ret = bnode;
				bnode = null;
				break;

			case LITERAL:
				ret = literal;
				literal = null;
				break;

			case FORMULA:
				ret = formula;
				formula = null;
				break;

			case QUICKVAR:
				ret = quickVar;
				quickVar = null;
				break;

			case COLLECTION:
				ret = collection;
				collection = null;
				break;
			}

			itemType = -1;

			return ret;
		}

		@Override
		public String toString() {
			return pathCnt + " - " + Arrays.toString(triple);
		}
	}
}