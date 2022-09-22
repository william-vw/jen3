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

package org.apache.jen3.n3.impl;

import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_STATIC_DATA;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_INFERENCE_FUSE;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_UNSUPP_BACKWARD;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_UNSUPP_BECOMES;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_UNSUPP_FORWARD;
import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.jen3.enhanced.EnhNode;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Graph.GraphConfig;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.N3CitedGraph;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Quantifier;
import org.apache.jen3.graph.n3.Quantifiers;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.n3.N3Feedback;
import org.apache.jen3.n3.N3MistakeTypes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.impl.N3Rule.RuleDirs;
import org.apache.jen3.n3.impl.N3Rule.RuleLogics;
import org.apache.jen3.n3.io.N3JenaWriter;
import org.apache.jen3.n3.scope.N3ScopeScheme;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.QuantifiedVariable;
import org.apache.jen3.rdf.model.Quantifier;
import org.apache.jen3.rdf.model.QuickVariable;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.RDFWriter;
import org.apache.jen3.rdf.model.ReifiedStatement;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.rdf.model.impl.IteratorFactory;
import org.apache.jen3.rdf.model.impl.ModelCom;
import org.apache.jen3.rdf.model.impl.QuantifierImpl;
import org.apache.jen3.reasoner.BaseInfGraph;
import org.apache.jen3.reasoner.Derivation;
import org.apache.jen3.reasoner.InfGraph;
import org.apache.jen3.reasoner.InfGraph.DeductionListener;
import org.apache.jen3.reasoner.Reasoner;
import org.apache.jen3.reasoner.ValidityReport;
import org.apache.jen3.reasoner.rulesys.ForwardRuleInfGraphI;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.vocabulary.N3Log;

/**
 * Implementation of an N3 model.
 * 
 * (based on OntModelImpl)
 * 
 * This could be a subclass of InfModelImpl, but an N3 model does not
 * necessarily perform inferencing.
 * 
 * 
 * @author wvw
 *
 */

public class N3ModelImpl extends ModelCom implements N3Model, DeductionListener {

	private boolean immutable = false;

	private N3ModelSpec spec;

	// active set of rules for this model
	private N3RuleBase ruleBase = new N3RuleBase();

	private Scope scope;

	private N3ScopeScheme scopeScheme;

	/** Cached deductions model */
	private Model deductionsModel = null;

	private N3EventListener listener;

	/**
	 * Construct a new N3 model from the given specification. The base model is
	 * produced using the spec's model maker.
	 */
	public N3ModelImpl(N3ModelSpec spec) {
		this(spec, spec.createBaseModel());
	}

	public N3ModelImpl(N3ModelSpec spec, Model base) {
		this(spec, base.getGraph());
	}

	public N3ModelImpl(N3ModelSpec spec, Graph graph) {
		this.spec = spec;

		N3GraphConfig graphConfig = new N3GraphConfig(spec.getBuiltinConfig(), spec.getFeedbacks());
		graph.setConfig(graphConfig);

		this.graph = generateGraph(spec, graph);

		this.scopeScheme = N3ScopeScheme.create(spec.getScopeType(), this);

		// will be overwritten for cited formulas
		Scope.outer().attach(this);

		initialize();
	}

	private void initialize() {
		if (spec.loadBuiltins())
			spec.getBuiltinConfig().initialize();
	}

	private Graph generateGraph(N3ModelSpec spec, Graph base) {
		Reasoner r = spec.getReasoner();

		// if we have a reasoner in the spec, bind to the graph and return
		return (r == null ? (Graph) base : r.bind(base, this));
	}

	@Override
	public N3ModelSpec getSpec() {
		return spec;
	}

	@Override
	public void setImmutable() {
		this.immutable = true;
	}

	@Override
	public boolean isImmutable() {
		return immutable;
	}

	@Override
	public N3Model copy() {
		if (!(graph instanceof N3CitedGraph))
			throw new UnsupportedOperationException("can only copy N3 models with N3CitedGraph");

		N3CitedGraph cg = (N3CitedGraph) graph;
		return ModelFactory.createN3Model(spec, cg.copy());
	}

	@Override
	public void setListener(N3EventListener listener) {
		this.listener = listener;
	}

	/**
	 * Using this method is often a mistake. Add statements from an N3
	 * serialization. It is generally better to use an InputStream if possible,
	 * otherwise there is a danger of a mismatch between the character encoding of
	 * say the FileReader and the character encoding of the data in the file.
	 * 
	 * <p>
	 * See <a href="http://jena.apache.org/documentation/io/index.html">"Reading and
	 * Writing RDF in Apache Jena"</a> for more information about concrete syntaxes.
	 * </p>
	 * 
	 * @param reader
	 * @param base   the base uri to be used when converting relative URI's to
	 *               absolute URI's and to guess the RDF serialization syntax.
	 * @return the current model
	 */

	@Override
	public Model read(Reader reader, String base) {
		return read(reader, base, "N3");
	}

	/**
	 * Add statements from a document. This method assumes the concrete syntax is
	 * N3.
	 * <p>
	 * See <a href="http://jena.apache.org/documentation/io/index.html">"Reading and
	 * Writing RDF in Apache Jena"</a> for more information about concrete syntaxes.
	 * </p>
	 * 
	 * @param in   the input stream
	 * 
	 * @param base the base uri to be used when converting relative URI's to
	 *             absolute URI's. (Resolving relative URIs and fragment IDs is done
	 *             by prepending the base URI to the relative URI/fragment.) If
	 *             there are no relative URIs in the source, this argument may
	 *             safely be <code>null</code>. If the base is the empty string,
	 *             then relative URIs <i>will be retained in the model</i>. This is
	 *             typically unwise and will usually generate errors when writing
	 *             the model back out.
	 * 
	 * @return the current model
	 */
	@Override
	public Model read(InputStream reader, String base) {
		return read(reader, base, "N3");
	}

	/**
	 * Add the RDF statements from an N3 document.
	 * <p>
	 * See {@link Model} for a description of how to traverse a firewall.
	 * </p>
	 *
	 * @return this model
	 * @param url of the N3 document containing the RDF statements.
	 */
	@Override
	public Model read(String url) {
		return read(url, "N3");
	}

	@Override
	public Resource createResource() {
		return IteratorFactory.asResource(NodeFactory.createBlankNode(scope), this);
	}

	/**
	 * add a statement to this model.
	 * 
	 * ensures that the scope of a quantified variable will be set properly; if they
	 * already have a scope and it is inconsistent with this model, a warning will
	 * be given and the scope will be overwritten.
	 * 
	 * @return this model
	 * @param s the subject of the statement to add
	 * @param p the predicate of the statement to add
	 * @param o the object of the statement to add
	 */

	@Override
	public Model add(Resource s, Resource p, Resource o) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		if (setup(s, p, o)) {
			ruleBase.onAdd(s, p, o);
			return super.add(s, p, o);
		} else
			return this;
	}

	@Override
	public Model add(Statement[] statements) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		for (Statement stmt : statements) {
			if (setup(stmt)) {
				ruleBase.onAdd(stmt);
				super.add(stmt);
			}
		}

		return this;
	}

	@Override
	public Model add(List<Statement> statements) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		statements.stream().forEach(stmt -> {
			if (setup(stmt)) {
				ruleBase.onAdd(stmt);
				super.add(stmt);
			}
		});

		return this;
	}

	@Override
	public Model add(StmtIterator iter) {
		return add(iter.toList());
	}

	// in this remove method and ones below:
	// we really need to call graph.remove since only that one will rebind and hence
	// properly support reasoning (ModelCom#remove methods will call graph.delete,
	// which doesn't do that)

	@Override
	public Model remove(Resource s, Resource p, Resource o) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		reset(s, p, o);

		// Model m = super.remove(s, p, o);
		graph.remove((s != null ? s.asNode() : null), (p != null ? p.asNode() : null), (o != null ? o.asNode() : null));

		ruleBase.onRemove(s, p, o);

		return this;
	}

	@Override
	public Model remove(Statement s) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		reset(s);

		// Model m = super.remove(s);
		remove(s.getSubject(), s.getPredicate(), s.getObject());

		ruleBase.onRemove(s);

		return this;
	}

	@Override
	public Model remove(StmtIterator iter) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		List<Statement> stmts = iter.toList();
		stmts.stream().forEach(s -> {
			reset(s);
			remove(s);
		});

		// iter = new StmtIteratorImpl(stmts.iterator());
		// Model m = super.remove(iter);

		ruleBase.onRemove(stmts);

		return this;
	}

	@Override
	public Model remove(Model m) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		intersection(m).listStatements().forEachRemaining(s -> {
			reset(s);
			remove(s);
		});

//		Model ret = super.remove(m);

		ruleBase.onRemove(m);

		return this;
	}

	@Override
	public Model removeAll() {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		listStatements().forEachRemaining(s -> reset(s));

		Model m = super.removeAll();

		ruleBase.onRemoveAll();

		return m;
	}

	@Override
	public Model removeAll(Resource s, Resource p, Resource o) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		listStatements(s, p, o).forEachRemaining(stmt -> reset(stmt));

		Model m = super.removeAll(s, p, o);

		ruleBase.onRemoveAll(s, p, o);

		return m;
	}

	@Override
	public Model remove(Statement[] statements) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		return remove(Arrays.asList(statements));
	}

	@Override
	public Model remove(List<Statement> statements) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		statements.stream().forEach(s -> {
			reset(s);
			remove(s);
		});

		// Model m = super.remove(statements);

		ruleBase.onRemove(statements);

		return this;
	}

	@Override
	public void removeAllReifications(Statement s) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		super.removeAllReifications(s);
	}

	@Override
	public void removeReification(ReifiedStatement rs) {
		if (immutable)
			throw new UnsupportedOperationException("Attempting to change an immutable N3 model.");

		super.removeReification(rs);
	}

	/** START scoping part **/

	@Override
	public boolean hasScope() {
		return scope != null;
	}

	@Override
	public void setScope(Scope scope) {
		this.scope = scope;
		graph.setScope(scope);
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	private boolean setup(Statement stmt) {
		return setup(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
	}

	private boolean setup(Resource s, Resource p, Resource o) {
		if (!setup(s) || !setup(p, true) || !setup(o))
			return false;

		return true;
	}

	private boolean setup(Resource r) {
		return setup(r, false);
	}

	private boolean setup(Resource r, boolean pred) {
		if (pred && scope.isOuter() && spec.getBuiltinSet().isStatic(r.asNode())
				&& spec.hasFeedbackFor(BUILTIN_STATIC_DATA)) {

			spec.getFeedback(BUILTIN_STATIC_DATA).doDefaultAction(r.getURI());

//			return false;
		}

		if (r.isCitedFormula()) {
			CitedFormula cf = r.asCitedFormula();
			if (!cf.isClosed())
				cf.close();

		} else if (r.isCollection()) {
			Collection col = r.asCollection();
			if (!col.isClosed())
				col.close();
		}

		return true;
	}

	private void reset(Statement s) {
//		reset(s.getSubject());
//		reset(s.getPredicate());
//		reset(s.getObject());
	}

	private void reset(Resource... rs) {
//		for (Resource r : rs)
//			reset(r);
	}

//	private void reset(Resource r) {
//		Scope toRemove = null;
//		switch (r.getNodeType()) {
//
//		case CITED_FORMULA:
//			toRemove = r.asCitedFormula().getScope();
//			break;
//
//		default:
//			break;
//		}
//
//		if (toRemove != null)
//			scope.removeChild(toRemove);
//	}

	@Override
	public void onDeduction(Triple t) {
//		Log.info(getClass(), "onDeduction? " + t);
		ruleBase.onAdd(t);
	}

	/**
	 * <p>
	 * Write the model as an N3 document. It is often better to use an OutputStream
	 * rather than a Writer, since this will avoid character encoding errors.
	 * </p>
	 *
	 * @param writer A writer to which the N3 will be written
	 * @return this model
	 */
	@Override
	public Model write(Writer writer) {
		return write(writer, "N3");
	}

	/**
	 * <p>
	 * Write a serialization of this model as an N3 document.
	 * </p>
	 * 
	 * @param out The output stream to which the N3 will be written
	 * @return This model
	 */
	@Override
	public Model write(OutputStream out) {
		return write(out, "N3");
	}

	/**
	 * Return the raw RDF model being processed (i.e. the argument to the
	 * Reasonder.bind call that created this InfModel).
	 */
	@Override
	public Model getRawModel() {
		return ModelFactory.createModelForGraph(graph);
	}

	/**
	 * Return the Reasoner which is being used to answer queries to this graph.
	 */
	@Override
	public Reasoner getReasoner() {
		return (graph instanceof InfGraph) ? ((InfGraph) graph).getReasoner() : null;
	}

	/**
	 * Cause the inference model to reconsult the underlying data to take into
	 * account changes. Normally changes are made through the InfModel's add and
	 * remove calls are will be handled appropriately. However, in some cases
	 * changes are made "behind the InfModels's back" and this forces a full
	 * reconsult of the changed data.
	 */
	@Override
	public void rebind() {
		if (graph instanceof InfGraph) {
			((InfGraph) graph).rebind();
		}
	}

	/**
	 * Perform any initial processing and caching. This call is optional. Most
	 * engines either have negligable set up work or will perform an implicit
	 * "prepare" if necessary. The call is provided for those occasions where
	 * substantial preparation work is possible (e.g. running a forward chaining
	 * rule system) and where an application might wish greater control over when
	 * this prepration is done rather than just leaving to be done at first query
	 * time.
	 */
	@Override
	public void prepare() {
		if (graph instanceof InfGraph) {
			((InfGraph) graph).prepare();
		}
	}

	public void prepared() {
		if (graph instanceof InfGraph) {
			((BaseInfGraph) graph).setPreparedState(true);
		}
	}

	/**
	 * Reset any internal caches. Some systems, such as the tabled backchainer,
	 * retain information after each query. A reset will wipe this information
	 * preventing unbounded memory use at the expense of more expensive future
	 * queries. A reset does not cause the raw data to be reconsulted and so is less
	 * expensive than a rebind.
	 */
	@Override
	public void reset() {
		if (graph instanceof InfGraph) {
			((InfGraph) graph).reset();
		}
	}

	/**
	 * Test the consistency of the underlying data. This normally tests the validity
	 * of the bound instance data against the bound schema data.
	 * 
	 * @return a ValidityReport structure
	 */
	@Override
	public ValidityReport validate() {
		return (graph instanceof InfGraph) ? ((InfGraph) graph).validate() : null;
	}

	/**
	 * Find all the statements matching a pattern.
	 * <p>
	 * Return an iterator over all the statements in a model that match a pattern.
	 * The statements selected are those whose subject matches the
	 * <code>subject</code> argument, whose predicate matches the
	 * <code>predicate</code> argument and whose object matches the
	 * <code>object</code> argument. If an argument is <code>null</code> it matches
	 * anything.
	 * </p>
	 * <p>
	 * The s/p/o terms may refer to resources which are temporarily defined in the
	 * "posit" model. This allows one, for example, to query what resources are of
	 * type CE where CE is a class expression rather than a named class - put CE in
	 * the posit arg.
	 * </p>
	 *
	 * @return an iterator over the subjects
	 * @param subject   The subject sought
	 * @param predicate The predicate sought
	 * @param object    The value sought
	 * @param posit     Model containing additional assertions to be considered when
	 *                  matching statements
	 */
	@Override
	public StmtIterator listStatements(Resource subject, Resource predicate, Resource object, Model posit) {
		if (graph instanceof InfGraph) {
			Graph gp = (posit == null ? ModelFactory.createDefaultModel().getGraph() : posit.getGraph());
			Iterator<Triple> iter = getInfGraph().find(asNode(subject), asNode(predicate), asNode(object), gp);
			return IteratorFactory.asStmtIterator(iter, this);

		} else {
			return null;
		}
	}

	// overridden these methods to ensure that they use our asResource() method

	@Override
	public CitedFormula createCitedFormula() {
		Scope scope = this.scope.sub(Scopes.GRAPH);

		return (CitedFormula) newResource(NodeFactory.createCitedFormula(scope));
	}

	public CitedFormula createCitedFormula(N3Model model) {
		Scope scope = this.scope.sub(Scopes.GRAPH);

		return (CitedFormula) newResource(NodeFactory.createCitedFormula(scope, model));
	}

	public CitedFormula createCitedFormula(N3ModelSpec spec) {
		Scope scope = this.scope.sub(Scopes.GRAPH);

		return (CitedFormula) newResource(NodeFactory.createCitedFormula(scope, spec));
	}

//	@Override
//	public CitedFormula createCitedFormula(Scope scope, N3Model model) {
//		CitedFormula cf = (CitedFormula) newResource(NodeFactory.createCitedFormula(scope, model));
//		cf.close();
//
//		return cf;
//	}

	@Override
	public QuickVariable createQuickVariable(String name) {
		return (QuickVariable) newResource(NodeFactory.createQuickVariable(name,
				(Node_Quantifier) createQuantifier(Quantifiers.UNIVERSAL).asNode()));
	}

	private Quantifier createQuantifier(Quantifiers quantifier) {
		return new QuantifierImpl(NodeFactory.createQuantifier(quantifier), this);
	}

	@Override
	public Collection createCollection() {
		return (Collection) newResource(NodeFactory.createCollection());
	}

	@Override
	public Collection createCollection(List<Resource> elements) {
		List<Node> nodes = elements.stream().map(e -> e.asNode()).collect(Collectors.toList());

		return (Collection) newResource(NodeFactory.createCollection(nodes));
	}

	@Override
	public Collection createCollection(Resource... elements) {
		return createCollection(new ArrayList<>(Arrays.asList(elements)));
	}

	@Override
	public RDFObject asRDFObject(Node n) {
		switch (n.getType()) {

		case QUANTIFIER:
			return this.getNodeAs(n, Quantifier.class);

		default:
			return asResource(n);
		}
	}

	private Class<? extends Resource> getCls(Node n) {
		switch (n.getType()) {
		case URI:
		case BLANK:
			return Resource.class;

		case LITERAL:
			return Literal.class;

		case COLLECTION:
			return Collection.class;

		case CITED_FORMULA:
			return CitedFormula.class;

		case QUANT_VAR:
			return QuantifiedVariable.class;

		case QUICK_VAR:
		case RULEVAR:
			return QuickVariable.class;

		default:
			return null;
		}
	}

	@Override
	public Resource asResource(Node n) {
		Class<? extends Resource> cls = getCls(n);

		// i.e., does it make sense to cache it? ..
		if (!n.doCache())
			return create(n, cls);

		RDFObject o = getCached(n);
		// if directly cached in model, re-use it
		if (o != null)
			return ((EnhNode) o).viewAs(cls);
		else
			// else, create new and return it
			return createCached(n, cls);

		// return getNodeAs(n, cls);
	}

	protected Resource newResource(Node n) {
		Class<? extends Resource> cls = getCls(n);

		// i.e., does it make sense to cache it? ..
		if (!n.doCache())
			return create(n, cls);

		// first, let scope-scheme (if any) have a go at it
		if (scopeScheme != null) {
			Resource r = scopeScheme.asResource(n, cls);
			if (r != null)
				return r;
		}

		// else, proceed as usual
		RDFObject o = getCached(n);
		// if directly cached in model, re-use it
		if (o != null)
			return ((EnhNode) o).viewAs(cls);
		else
			// else, create new and return it
			return createCached(n, cls);

		// return getNodeAs(n, cls);
	}

	@Override
	public RDFObject getCachedNested(Node n) {
		RDFObject o = cache.getIfPresent(n);
		if (o != null)
			return o;

		else if (scope.hasParent()) {
			N3Model outer = (N3Model) scope.getParent().getScoped();
			if (outer != null)
				return outer.getCachedNested(n);
		}

		return null;
	}

	/**
	 * Switch on/off drivation logging. If this is switched on then every time an
	 * inference is a made that fact is recorded and the resulting record can be
	 * access through a later getDerivation call. This may consume a lot of space!
	 */
	@Override
	public void setDerivationLogging(boolean logOn) {
		if (graph instanceof InfGraph) {
			((InfGraph) graph).setDerivationLogging(logOn);
		}
	}

	/**
	 * Return the derivation of the given statement (which should be the result of
	 * some previous list operation). Not all reasoneers will support derivations.
	 * 
	 * @return an iterator over Derivation records or null if there is no derivation
	 *         information available for this triple.
	 */
	@Override
	public Iterator<Derivation> getDerivation(Statement statement) {
		return (graph instanceof InfGraph) ? ((InfGraph) graph).getDerivation(statement.asTriple()) : null;
	}

	/**
	 * <p>
	 * Returns a derivations model. The rule reasoners typically create a graph
	 * containing those triples added to the base graph due to rule firings. In some
	 * applications it can useful to be able to access those deductions directly,
	 * without seeing the raw data which triggered them. In particular, this allows
	 * the forward rules to be used as if they were rewrite transformation rules.
	 * </p>
	 *
	 * @return The derivations model, if one is defined, or else null
	 */
	@Override
	public Model getDeductionsModel() {
		Graph deductionsGraph = getInfGraph().getDeductionsGraph();
		if (deductionsGraph != null) {
			if (deductionsModel == null || (deductionsModel.getGraph() != deductionsGraph)) {
				deductionsModel = new N3ModelImpl(N3ModelSpec.get(N3_MEM), deductionsGraph);
			}
		}
		return deductionsModel;
	}

	/**
	 * <p>
	 * Answer the InfGraph that this model is wrapping, or null if this ontology
	 * model is not wrapping an inf graph.
	 * </p>
	 * 
	 * @return The model's graph as an InfGraph, or null
	 */
	private InfGraph getInfGraph() {
		return (graph instanceof InfGraph) ? ((InfGraph) graph) : null;
	}

	@Override
	public RDFWriter getWriter(String lang) {
		System.setProperty("http://jena.hpl.hp.com/n3/properties/writer", N3JenaWriter.n3WriterFullPrinter);
//		System.setProperty("http://jena.hpl.hp.com/n3/properties/writer", N3JenaWriter.n3WriterPrettyPrinter);

		return super.getWriter(lang);
	}

	@Override
	public String outputString() {
		SortedMap<String, String> sorted = new TreeMap<>();
		listStatements(null, N3Log.outputString, (Resource) null).forEachRemaining(stmt -> {
			String str = stmt.getObject().asLiteral().getString();
			// N3 code requires all non-standard escapes to be themselves escaped
			// undo this double-escaping in output strings
			str = str.replaceAll(Pattern.quote("\\\\") + "(.)", "\\\\$1");
			str = StringEscapeUtils.unescapeJava(str);

			sorted.put(stmt.getSubject().toString(), str);
		});

		return sorted.values().stream().collect(Collectors.joining(""));
	}

	@Override
	public void outputString(OutputStream out) throws IOException {
		out.write(outputString().getBytes());
	}

	@Override
	public String toString() {
		String stats = graph.getStats().toString();

		return "<N3Model" + (stats.isEmpty() ? "" : " " + stats) + " - " + getGraph() + ">";
	}

	/**
	 * Represents the "rule base" for this N3 model. Represents the bridge between
	 * graph implications and Jena n3 rules (doesn't actually keep any rules to
	 * minimize housekeeping; FRuleEngine already keeps rules twice).
	 * 
	 * Whenever statements are added or removed from the N3Model, the class event
	 * handlers should be called.
	 * 
	 * 
	 * @author wvw
	 *
	 */

	protected class N3RuleBase {

		// onAdd methods: called before anything has been added
		// (doesn't really matter when they are called)

		public void onAdd(Node s, Node p, Node o) {
			if (spec.isInf()) {
				RuleDirs dir = null;
				RuleLogics logic = null;

				if (p.equals(N3Log.implies.asNode())) {
					dir = RuleDirs.FORWARD;
					logic = RuleLogics.CLASSIC;

				} else if (p.equals(N3Log.impliedBy.asNode())) {
					dir = RuleDirs.BACKWARD;
					logic = RuleLogics.CLASSIC;

				} else if (p.equals(N3Log.becomes.asNode())) {
					dir = RuleDirs.FORWARD;
					logic = RuleLogics.LINEAR;

				} else if (p.equals(N3Log.comesFrom.asNode())) {
					dir = RuleDirs.BACKWARD;
					logic = RuleLogics.LINEAR;

				} else
					return;

				if (dir == RuleDirs.BACKWARD) {
					Node tmp = s;
					s = o;
					o = tmp;
				}

				switch (spec.getRuleMode().getName()) {

				case "forward":
				case "forwardRETE":
					if (dir == RuleDirs.BACKWARD && spec.hasFeedbackFor(INFER_UNSUPP_BACKWARD))
						spec.getFeedback(INFER_UNSUPP_BACKWARD).doDefaultAction(s, o);
					break;

				case "backward":
					if (dir == RuleDirs.FORWARD && spec.hasFeedbackFor(INFER_UNSUPP_FORWARD))
						spec.getFeedback(INFER_UNSUPP_FORWARD).doDefaultAction(s, o);
					break;

				case "hybrid":
					break;
				}

				if (logic == RuleLogics.LINEAR && spec.hasFeedbackFor(INFER_UNSUPP_BECOMES))
					spec.getFeedback(INFER_UNSUPP_BECOMES).doDefaultAction(s, o);

//				System.out.println("add.rule: " + s + " " + p + " " + o);
				add(s, o, dir, logic);
			}
		}

		public void onAdd(Resource s, Resource p, Resource o) {
			onAdd(s.asNode(), p.asNode(), o.asNode());
		}

		public void onAdd(Statement stmt) {
			onAdd(stmt.getSubject(), stmt.getPredicate(), stmt.getObject());
		}

		public void onAdd(Triple t) {
			onAdd(t.getSubject(), t.getPredicate(), t.getObject());
		}

		private void add(Node body, Node head, RuleDirs dir, RuleLogics logic) {
			N3Rule rule = new N3Rule(head, body, spec, dir, logic);

			// true =>
			// {} => (i.e., empty rule body)
			if ((Util.isBoolean(body) && Util.getBooleanValue(body))
					|| (body.isCitedFormula() && Util.getModelValue(body).isEmpty())) {

				// => false
				if (Util.isBoolean(head) && !Util.getBooleanValue(head) && spec.hasFeedbackFor(INFER_INFERENCE_FUSE))
					spec.getFeedback(INFER_INFERENCE_FUSE).doDefaultAction(rule);

				// => {..}
				else if (head.isCitedFormula()) {
					Node_CitedFormula inf = (Node_CitedFormula) head;

					ForwardRuleInfGraphI ig = (ForwardRuleInfGraphI) getInfGraph();
					inf.getContents().listStatements().forEachRemaining(stmt -> ig.addDeduction(stmt.asTriple()));
				}

				return;

				// false =>
				// ex falso quodlibet
			} else if (Util.isBoolean(body) && !Util.getBooleanValue(body))
				return;

			rule.parse();
//			System.out.println("rule? " + rule);

			// {..} => {..}
			// {..} => ?x
			// {..} => false
			if (body.isCitedFormula() && (head.isCitedFormula() || head.isVariable()
					|| (Util.isBoolean(head) && !Util.getBooleanValue(head)))) {

				// ignore rules with empty head
				if (head.isCitedFormula() && Util.getModelValue(head).isEmpty())
					return;

				getRuleInfGraph().addRule(rule);
				if (listener != null)
					listener.newRule(rule);

			} else
				spec.getFeedback(N3MistakeTypes.SYNTAX_MALFORMED_RULE).doDefaultAction(body, head);
		}

		// onRemove methods: called after anything has been added
		// (must be called after; need to compare current rules with prior ones)

		public void onRemove(Resource s, Resource p, Resource o) {
			onRemove();
		}

		public void onRemove(Statement stmt) {
			onRemove();
		}

		public void onRemove(List<Statement> stmts) {
			onRemove();
		}

		public void onRemove(Model m) {
			onRemove();
		}

		public void onRemoveAll(Resource s, Resource p, Resource o) {
			onRemove();
		}

		public void onRemoveAll() {
			onRemove();
		}

		public void onRemove() {
		}

		// TODO
		// currently it is assumed that users should really be careful when removing
		// statements from infgraphs - this code is resource-intensive.

		/*
		 * Called after any triple has been removed. In case an installed rule is no
		 * longer found in the graph, uninstall the rule and run this method again
		 * (rules can infer other rules, so latter ones must be removed too).
		 * 
		 * Note that BaseInfGraph#remove will call rebind after removing a single
		 * triple.
		 */

//		public void onRemove() {
//			if (!spec.isInf())
//				return;
//
//			BasicForwardRuleInfGraph bg = getRuleInfGraph();
//
//			List<Rule> rules = new ArrayList<>(bg.getRules());
//			rules.stream().map(r -> (N3Rule) r).forEach(rule -> {
		// TODO very important to do this containment check depending on type of rule
		// e.g., linear logic, backward, ..
//				if (!bg.contains(rule.getBodyNode(), N3Log.implies.asNode(), rule.getHeadNode())) {
//
//					if (bg.removeRule(rule))
//						onRemove();
//
//					return;
//				}
//			});
//		}

		protected BaseInfGraph getRuleInfGraph() {
			return (BaseInfGraph) getInfGraph();
		}
	}

	public static class N3GraphConfig implements GraphConfig {

		private BuiltinConfig builtinConfig;

		private Map<N3MistakeTypes, N3Feedback> feedbacks = new HashMap<>();

		public N3GraphConfig(BuiltinConfig builtinConfig, Map<N3MistakeTypes, N3Feedback> feedbacks) {

			this.builtinConfig = builtinConfig;
			this.feedbacks = feedbacks;
		}

		public BuiltinConfig getBuiltinConfig() {
			return builtinConfig;
		}

		public BuiltinSet getBuiltinSet() {
			return builtinConfig.getSet();
		}

		public boolean hasFeedbackFor(N3MistakeTypes... mistakes) {
			return Arrays.stream(mistakes).anyMatch(m -> feedbacks.containsKey(m));
		}

		public N3Feedback getFeedback(N3MistakeTypes mistake) {
			return feedbacks.get(mistake);
		}

		public Map<N3MistakeTypes, N3Feedback> getFeedbacks() {
			return feedbacks;
		}
	}

	public interface N3EventListener {

		public void newRule(N3Rule r);
	}
}