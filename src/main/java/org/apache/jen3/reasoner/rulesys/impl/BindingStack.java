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

package org.apache.jen3.reasoner.rulesys.impl;

import static org.apache.jen3.n3.N3MistakeTypes.INFER_UNBOUND_GLOBALS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.BlankNodeId;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_ANY;
import org.apache.jen3.graph.Node_Blank;
import org.apache.jen3.graph.Node_Compound;
import org.apache.jen3.graph.Node_Literal;
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.impl.LiteralLabel;
import org.apache.jen3.graph.n3.ModifyFormulaNodeVisitor;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.impl.N3Rule;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.Functor;
import org.apache.jen3.reasoner.rulesys.Functor.FunctorDatatype;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.reasoner.rulesys.RuleContext;
import org.apache.jena.atlas.logging.Log;

/**
 * Provides a trail of possible variable bindings for a forward rule.
 */
public class BindingStack implements BindingEnvironment {

	// We used to have a strange implmentation that avoided GC overheads
	// by doing copying up a fixed-width stack. The interface to this object
	// is weird because of this, though the current implementation is a little
	// more normal

	/** The current binding set */
	protected Node[] environment;

	/** A stack of prior binding sets */
	protected ArrayList<Node[]> trail = new ArrayList<>();

	/** Index of the current binding set */
	protected int index = 0;

	protected RuleContext ctx;
	protected InstantiateTriplePatternVisitor instVisitor;

	/** The associated forward rule **/
	protected Rule rule;

	/**
	 * Constructor. The stack isn't ready for use until reset has been called.
	 */
	public BindingStack(RuleContext ctx) {
		index = 0;
		this.ctx = ctx;

		init();
	}

	protected BindingStack(Node[] environment) {
		this.environment = environment;

		init();
	}

	public BindingStack() {
		index = 0;
		this.environment = new Node[] {};

		init();
	}

	protected void init() {
		instVisitor = new InstantiateTriplePatternVisitor();
	}

	public RuleContext getCtx() {
		return ctx;
	}

	/**
	 * Save the current environment on an internal stack
	 */
	public void push() {
		if (trail.size() > index) {
			trail.set(index, environment);
		} else {
			trail.add(environment);
		}
		index++;
		Node[] newenv = new Node[environment.length];
		System.arraycopy(environment, 0, newenv, 0, environment.length);
		environment = newenv;
	}

	/**
	 * Forget the current environment and return the previously pushed state.
	 * 
	 * @throws IndexOutOfBoundsException if there was not previous push
	 */
	public void unwind() throws IndexOutOfBoundsException {
		if (index > 0) {
			// just point to previous stack entry
			environment = trail.get(--index);
			trail.set(index, null); // free the old space for GC
		} else {
			throw new IndexOutOfBoundsException("Underflow of BindingEnvironment");
		}
	}

	/**
	 * Forget the previously pushed state but keep the current environment.
	 * 
	 * @throws IndexOutOfBoundsException if there was not previous push
	 */
	public void commit() throws IndexOutOfBoundsException {
		if (index > 0) {
			trail.set(--index, null);
		} else {
			throw new IndexOutOfBoundsException("Underflow of BindingEnvironment");
		}
	}

	/**
	 * Reset the binding environment to empty.
	 * 
	 * @param newSize the number of variables needed for processing the new rule
	 */
	public void reset(int newSize) {
		index = 0;
		trail.clear();
		environment = new Node[newSize];
	}

	/**
	 * Expand the binding environment.
	 * 
	 * @param newSize the new number of variables needed for processing the new rule
	 */
	public void expand(int newSize) {
		if (newSize == environment.length)
			return;

		Node[] newEnv = new Node[newSize];
		System.arraycopy(environment, 0, newEnv, 0, environment.length);

		environment = newEnv;
		trail.set(index - 1, environment);
	}

	/**
	 * Return the current array of bindings
	 */
	public Node[] getEnvironment() {
		return environment;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public Node ground(Node node) {
		return ground(node, groundFull);
	}

	public Node ground(Node node, GroundCmd cmd) {
		Node binding = getBinding(node);
		// rule variable but not bound
		if (binding == null)
			return node;

		return instVisitor.visitNode(binding, cmd);
	}

	public Node groundShallow(Node node) {
		Node binding = getBinding(node);
		// rule variable but not bound
		if (binding == null)
			return node;

		return binding;
	}

	public Node groundConcrete(Node binding, GroundCmd cmd) {
		return instVisitor.visitNode(binding, cmd);
	}

	public Node getBinding(Node node) {
		if (node.isRuleVariable()) {
			Node_RuleVariable var = (Node_RuleVariable) node;
			return environment[var.getIndex()];
		} else
			return node;
	}

	@Override
	public boolean isBound(Node node) {
		if (node.isRuleVariable()) {
			Node_RuleVariable var = (Node_RuleVariable) node;
			return environment[var.getIndex()] != null;

		} else
			return false;
	}

	/**
	 * Bind the ith variable in the current envionment to the given value. Checks
	 * that the new binding is compatible with any current binding.
	 * 
	 * @return false if the binding fails
	 */
	public boolean bind(int i, Node value) {
		Node node = environment[i];
		if (node == null) {
			environment[i] = value;
			return true;
		} else {
			return node.sameValueAs(value);
		}
	}

	/**
	 * Bind a variable in the current envionment to the given value. Checks that the
	 * new binding is compatible with any current binding.
	 * 
	 * @param var   a Node_RuleVariable defining the variable to bind
	 * @param value the value to bind
	 * @return false if the binding fails
	 */
	@Override
	public boolean bind(Node var, Node value) {
		if (var instanceof Node_RuleVariable) {
			return bind(((Node_RuleVariable) var).getIndex(), value);
		} else {
			return var.sameValueAs(value);
		}
	}

	/**
	 * Bind a variable in the current envionment to the given value. Overrides and
	 * ignores any current binding.
	 * 
	 * @param var   a Node_RuleVariable defining the variable to bind
	 * @param value the value to bind
	 */
	public void bindNoCheck(Node_RuleVariable var, Node value) {
		environment[var.getIndex()] = value;
	}

	@Override
	public Triple instantiateOutput(TriplePattern pattern, Rule rule) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Triple> instantiateOutput(Rule rule) {
		return instVisitor.generateData((N3Rule) rule);
	}

	public List<Triple> instantiateOutput(Node_CitedFormula cf) {
		return instVisitor.generateData(cf);
	}

	public Triple instantiate(TriplePattern tp) {
		return instVisitor.instantiate(tp);
	}

	public BindingStack copy() {
		Node[] newEnv = new Node[environment.length];
		System.arraycopy(environment, 0, newEnv, 0, newEnv.length);

		return new BindingStack(newEnv);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BindingStack))
			return false;

		Node[] env2 = ((BindingStack) o).getEnvironment();

		// we're happy as long as they have the same contents
		// (binding envs can be extended as data with variables is pulled in)

//		if (env2.length != environment.length)
//			return false;

		for (int i = 0; i < environment.length; i++) {
			Node n1 = environment[i];
			Node n2 = (i < env2.length ? env2[i] : null);

			if (n1 == null ^ n2 == null || (n1 != null && !n1.equals(n2)))
				return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(environment);
	}

	/**
	 * This visitor is used for two purposes; to get bindings for body nodes during
	 * reasoning, and instantiating rule heads once the body is matched.
	 * 
	 * 
	 * @author wvw
	 *
	 */

	public static int nrNewGraphs = 0;
	public static int nrNewColls = 0;

	public class InstantiateTriplePatternVisitor extends ModifyFormulaNodeVisitor {

		protected Rule rule;
		// whether we're generating data that will be asserted in the KB
		protected boolean datagen = false;

		protected Scope curScope;

		// TODO
		// generalize this code (see other #instantiate)
		// requires changes to N3Rule

		public List<Triple> generateData(N3Rule rule) {
			this.rule = rule;

			datagen = true;

			List<Triple> ret = new ArrayList<>();

			for (int i = 0; i < rule.headLength(); i++) {
				Object hClause = rule.getHeadElement(i);
				if (hClause instanceof TriplePattern) {
					Triple t = instantiate((TriplePattern) hClause, null);
					ret.add(t);
				}
			}

			datagen = false;
//			bnodes.clear();

			return ret;
		}

		public List<Triple> generateData(Node_CitedFormula cf) {
			datagen = true;
			curScope = cf.getScope();

			List<Triple> ret = new ArrayList<>();

			// {..} => ?x
			cf.getContents().getGraph().find().forEachRemaining(tp -> {
				Triple t = instantiate(tp, null);
				ret.add(t);
			});

			datagen = false;
			curScope = null;

			return ret;
		}

		public Triple instantiate(TriplePattern pattern) {
			// not generating data to be asserted
			// (not needed to do all the fancy skolemization etc)

			Triple t = instantiate(pattern, null);

			return t;
		}

		private Triple instantiate(TriplePattern pattern, InstantiateData data) {
			Node s = visitNode(pattern.getSubject(), data);
			Node p = visitNode(pattern.getPredicate(), data);
			Node o = visitNode(pattern.getObject(), data);

			return new Triple(s, p, o);
		}

		private Triple instantiate(Triple pattern, InstantiateData data) {
			Node s = visitNode(pattern.getSubject(), data);
			Node p = visitNode(pattern.getPredicate(), data);
			Node o = visitNode(pattern.getObject(), data);

			return new Triple(s, p, o);
		}

		@Override
		public Node visitRuleVariable(Node_RuleVariable node, Object data) {
			InstantiateData inst = (InstantiateData) data;

			Node ret = null;

			// avoid infinite loops (see NOTES)
			if (inst.varTrace.contains(node.getName())) {
				ret = ruleVarToDataVar(node);

			} else {
				inst.varTrace.add(node.getName());

				Node bound = (node.getIndex() < environment.length ? environment[node.getIndex()] : null);

				if (bound == null)
					bound = node;
				else
					inst.bindingVar = true;

				if (!bound.isRuleVariable())
					ret = visitNode(bound, inst);

				// found an unbound rule-variable
				else
					ret = ruleVarToDataVar((Node_RuleVariable) bound);
			}

			return ret;
		}

		private Node ruleVarToDataVar(Node_RuleVariable node) {
			// we're generating output, where rule variables do not exist
			if (datagen) {

				// only generate witnesses for first level
				if (level == 0) {
					N3ModelSpec spec = ((N3Rule) rule).getModelSpec();
					if (node.getOriginal().isQuickVariable() && spec.hasFeedbackFor(INFER_UNBOUND_GLOBALS))
						spec.getFeedback(INFER_UNBOUND_GLOBALS).doDefaultAction(node.getOriginal(), rule);

					// generate witness
					Node witness = blankNodePerBindingEnv(node);
					return witness;

				} else {
					return node.getOriginal();
				}

			} else
				return node;
		}

		@Override
		public Node visitAny(Node_ANY node, Object data) {
			return null;
		}

		@Override
		public Node visitLiteral(Node_Literal node, LiteralLabel lit, Object data) {
			InstantiateData inst = (InstantiateData) data;

			if (node.getLiteralDatatype() == FunctorDatatype.theFunctorDatatype) {
				Functor functor = (Functor) node.getLiteralValue();
				if (functor.isGround())
					return node;

				Node[] args = functor.getArgs();
				List<Node> boundargs = new ArrayList<>(args.length);
				for (Node arg : args) {
					Node binding = visitNode(arg, inst);
					if (binding == null) {
						// Not sufficent bound to instantiate functor yet
						return null;
					}
					boundargs.add(binding);
				}
				Functor newf = new Functor(functor.getName(), boundargs, functor.getImplementor());
				return Functor.makeFunctorNode(newf);

			} else
				return node;
		}// see superclass for visitCitedFormula method

		@Override
		public Node visitCitedFormula(Node_CitedFormula it, Object data) {
			Node_Compound ret = null;

			InstantiateData inst = (InstantiateData) data;
			if (!requiresGrounding(inst))
				return it;

			if (it.includesRuleVars()) {
				nrNewGraphs++;

				Scope priorScope = curScope;
				curScope = it.getScope();

				ret = (Node_CitedFormula) super.visitCitedFormula(it, data);

				curScope = priorScope;

			} else
				ret = it;

			return ret;
		}

		@Override
		protected Triple visitFormulaContent(Triple t, Object data) {
			return instantiate(new TriplePattern(t), (InstantiateData) data);
		}

		@Override
		public Node visitCollection(Node_Collection it, Object data) {
			InstantiateData inst = (InstantiateData) data;

			if (!requiresGrounding(inst))
				return it;

			Node_Compound ret = null;
			if (it.includesVars()) {
				nrNewColls++;

				ret = new Node_Collection(
						it.getElements().stream().map(n -> visitNode(n, inst)).collect(Collectors.toList()));

			} else
				ret = it;

			return ret;
		}

		private boolean requiresGrounding(InstantiateData inst) {
			GroundCmd cmd = inst.ground;
			switch (cmd.option) {

			case LEVEL:
				if (cmd.decrLvl() == 0)
					return false;
				break;

			case NONE:
				return false;

			default:
				break;
			}

			return true;
		}

		@Override
		public Node visitVariable(Node_Variable it, String name, Object data) {
			return (level == 0 && datagen ? blankNodePerBindingEnv(it) : it);
		}

		@Override
		public Node visitQuickVariable(Node_QuickVariable it, Object data) {
			return (level == 0 && datagen ? blankNodePerBindingEnv(it) : it);
		}

		@Override
		public Node visitBlank(Node_Blank it, BlankNodeId id, Object data) {
			if (datagen) {
				InstantiateData inst = (InstantiateData) data;

				// if this was a blank node term originally given within the rule head,
				// then return a unique blank node for it

				if (inst.wasBound)
					// blank node comes from rule-var (wasBound = true)
					// (i.e., rule-var was bound to a (data) blank node in rule body)
					// need to skolemize this blank node (in case it originated from quoted graph)

					return skolemize(it);
				else
					// (needs to be unique per set of variable bindings)
					return blankNodePerBindingEnv(it);

			} else
				return it;
		}

		// per rule instantiation, the same blank node is returned for the same
		// existential / universal term in consequent (in the latter case, a witness)

		private Node blankNodePerBindingEnv(Node node) {
			// return a unique blank node per binding environment and original node

			// i.e., if rule head has multiple occurrences for a blank node, return same
			// blank node per binding environment

			Node bnode = ((N3Rule) rule).uniqueBlankNode(BindingStack.this, node, curScope);
//			Log.debug(getClass(), "new bnode: " + bnode + " - " + BindingStack.this);

			return bnode;
		}

		private Node skolemize(Node_Blank node) {
			// fine if originated from top-level (i.e., data)
			if (node.getScope() == null || node.getScope().getLvl() == 0) {
				return node;

			} else {
				// originated from quoted graph
				// return a unique skolem constant for this blank node
				Node skolem = ((N3Rule) rule).uniqueSkolem(node);
				Log.debug(getClass(), "new skolem: " + skolem);

				return skolem;
			}
		}

		protected Node visitNode(Node n, GroundCmd ground) {
			return n.visitWith(this, new InstantiateData(ground.copy()));
		}

		protected Node visitNode(Node n, InstantiateData data) {
			if (data == null)
				return n.visitWith(this, new InstantiateData(groundFull));
			else
				return n.visitWith(this, data.copy());
		}

		protected class InstantiateData {

			public GroundCmd ground;

			public List<String> varTrace = new ArrayList<>();
			public boolean bindingVar = false;
			public boolean wasBound = false;

			public InstantiateData(GroundCmd ground) {
				this.ground = ground;
			}

			public InstantiateData copy() {
				InstantiateData inst = new InstantiateData(ground.copy());

				inst.varTrace = new ArrayList<>(varTrace);
				// bindingVar set to true before visiting a bound value
				// only direct binding should be considered "bound"
				if (bindingVar)
					inst.wasBound = true;

				return inst;
			}
		}
	}

	public static GroundCmd groundFull = new GroundCmd(GroundOptions.FULL);
	public static GroundCmd groundNone = new GroundCmd(GroundOptions.NONE);

	public static enum GroundOptions {
		FULL, LEVEL, NONE
	}

	public static class GroundCmd {

		private GroundOptions option;

		private int lvl;

		public GroundCmd(GroundOptions option) {
			this.option = option;
		}

		public GroundCmd(int lvl) {
			this.option = GroundOptions.LEVEL;
			this.lvl = lvl;
		}

		public GroundOptions getOption() {
			return option;
		}

		public int curLvl() {
			return lvl;
		}

		public int decrLvl() {
			return lvl--;
		}

		public GroundCmd copy() {
			switch (option) {

			// only make new instance if it's mutable
			case LEVEL:
				return new GroundCmd(lvl);

			default:
				return this;
			}
		}
	}
}