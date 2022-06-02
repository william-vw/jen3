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

import java.util.ArrayList;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.InfGraph;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.ForwardRuleInfGraphI;
import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.reasoner.rulesys.RuleContext;
import org.apache.jen3.reasoner.rulesys.SilentAddI;
import org.apache.jen3.util.PrintUtil;
import org.apache.jen3.util.iterator.ClosableIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the generic RuleContext interface used by the basic
 * forward (BF) rule engine. This provides additional methods specific to the
 * functioning of that engine.
 */
public class BFRuleContext implements RuleContext {
	/**
	 * The binding environment which represents the state of the current rule
	 * execution.
	 */
	protected BindingStack env;

	/** The rule current being executed. */
	protected Rule rule;

	/** The enclosing inference graph. */
	protected ForwardRuleInfGraphI graph;

	/**
	 * A stack of triples which have been added to the graph but haven't yet been
	 * processed.
	 */
	protected List<Triple> stack;

	/**
	 * A temporary list of inferred Triples which will be added to the stack and
	 * deductions graph at the end of a rule scan
	 */
	protected List<Triple> pendingDeduct;

	/**
	 * A temporary list of asserted Triples (linear logic) that will be added to the
	 * stack and default graph at the end of a rule scan
	 */
//	protected List<Triple> pendingAssert;

	/**
	 * A temporary list of Triples which will be removed from the graph at the end
	 * of a rule scan
	 */
	protected List<Triple> deletesPending = new ArrayList<>();

	/** A searchable index into the pending triples */
//	protected Graph pendingCache;

	// support linear logic
	// (see #add, #remove methods)
	protected BFRuleContext parent;

	protected static Logger logger = LoggerFactory.getLogger(BFRuleContext.class);

	/**
	 * Constructor.
	 * 
	 * @param graph the inference graph which owns this context.
	 */
	public BFRuleContext(ForwardRuleInfGraphI graph) {
		this.graph = graph;
		env = new BindingStack(this);
//		env = new BindingStack_Cached(this);
		stack = new ArrayList<>();
		pendingDeduct = new ArrayList<>();
//		pendingAssert = new ArrayList<>();
//		pendingCache = Factory.createGraphMem();
	}

	public boolean hasParent() {
		return parent != null;
	}

	public BFRuleContext getParent() {
		return parent;
	}

	public void setParent(BFRuleContext parent) {
		this.parent = parent;
	}

	/**
	 * Returns the current variable binding environment for the current rule.
	 * 
	 * @return BindingEnvironment
	 */
	@Override
	public BindingEnvironment getEnv() {
		return env;
	}

	/**
	 * Variant of the generic getEnv interface specific to the basic forward rule
	 * system. Returns the current variable binding environment for the current
	 * rule.
	 * 
	 * @return BindingStack
	 */
	public BindingStack getEnvStack() {
		return env;
	}

	/**
	 * Returns the graph.
	 * 
	 * @return InfGraph
	 */
	@Override
	public InfGraph getGraph() {
		return graph;
	}

	/**
	 * Returns the rule.
	 * 
	 * @return Rule
	 */
	@Override
	public Rule getRule() {
		return rule;
	}

	public List<Triple> getStack() {
		return stack;
	}

	/**
	 * Sets the rule.
	 * 
	 * @param rule The rule to set
	 */
	@Override
	public void setRule(Rule rule) {
		this.rule = rule;

		env.setRule(rule);
	}

	/**
	 * Add a triple to the stack of triples to waiting to be processed by the rule
	 * engine.
	 */
	public void addTriple(Triple t) {
		if (graph.shouldTrace()) {
			if (rule != null) {
				logger.debug(
						"Adding to stack (" + rule.toShortString() + "): " + PrintUtil.print(t));
			} else {
				logger.debug("Adding to stack : " + PrintUtil.print(t));
			}
		}
		stack.add(t);
	}

	/**
	 * Add a triple to a temporary "pending" store, ready to be added to added to
	 * the deductions graph and the processing stack later.
	 * <p>
	 * This is needed to prevent concurrent modification exceptions which searching
	 * the deductions for matches to a given rule.
	 */
	@Override
	public void add(Triple t) {
		/*
		 * Inferred rules may also infer triples, of course. Other rules may be
		 * triggered by this inferred triple. Hence, after the inferred rule has been
		 * setup and had its initial inferences made, add the triple to the "top-level"
		 * context stack so it may trigger any rule (including the inferred rule).
		 */
		if (parent != null)
			parent.add(t);
		else {
//			if (deduction)
			pendingDeduct.add(t);
//			else
//				pendingAssert.add(t);

//			pendingCache.add(t);
		}
	}

	/**
	 * Remove a triple from the deduction graph (and the original graph if
	 * relevant).
	 */

	@Override
	public void remove(Triple t) {
		removeAndCheck(t);
	}

	// returns true in case this triple was not yet queued for deletion
	public boolean removeAndCheck(Triple t) {
		if (!deletesPending.contains(t)) {
			stack.remove(t);

			deletesPending.add(t);

			/*
			 * Rules can infer other rules (recursively), which may be linear logic rules.
			 * Such an inferred linear logic rule may withdraw a triple that is still on the
			 * stacks of higher-up-in-recursion rules (i.e., being iterated over for those
			 * rules).
			 * 
			 * So, when removing a triple, cascade that removal up to stacks of the "upper"
			 * inferring rules. However, directly queue it up to be removed from the graph
			 * after this rule firing, so it doesn't lead to redundant rule firings.
			 */
			if (parent != null) {
				parent.removeRecursively(t);
			}

			return true;

		} else
			return false;
	}

	private void removeRecursively(Triple t) {
		stack.remove(t);

		if (parent != null) {
			parent.removeRecursively(t);
		}
	}

	/**
	 * Take all the pending triples and add them to both the given graph and to the
	 * processing stack.
	 */
	public void flushPending() {
		// an inferred rule may have added its own inferred triples to pending
		// and we want to flush all of these (this is why we're not using an iterator!)
		while (!pendingDeduct.isEmpty()) {
			Triple t = pendingDeduct.remove(0);
			stack.add(t);
			graph.addDeduction(t);
//			pendingCache.delete(t);
		}

		// pending triples to be asserted
//		while (!pendingAssert.isEmpty()) {
//			Triple t = pendingAssert.remove(0);
//			stack.add(t); // also add these to the stack
//			graph.add(t);
//		}

		// Flush out pending removes as well
		for (Triple t : deletesPending) {
			graph.delete(t);
		}
		deletesPending.clear();
	}

	/**
	 * Return true if the triple is already in either the graph or the stack. I.e.
	 * it has already been deduced.
	 */

	// TODO test variable inferences on p/o positions
	// (see also GraphTripleStoreBase.find)
	@Override
	public boolean contains(Triple t) {
		// Can't use stackCache.contains because that does not do semantic equality
		setupFind(t.getSubject());
		boolean ret = contains(t.getSubject(), t.getPredicate(), t.getObject());
		resetFind(t.getSubject());

		return ret;
	}

	private void setupFind(Node n) {
		n.setMatchAbsolute(true);
	}

	private void resetFind(Node n) {
		n.setMatchAbsolute(false);
	}

	/**
	 * Return true if the triple pattern is already in either the graph or the
	 * stack. I.e. it has already been deduced.
	 */
	@Override
	public boolean contains(Node s, Node p, Node o) {
		// Can't use stackCache.contains because that does not do semantic equality
		ClosableIterator<Triple> it = find(s, p, o);
		boolean result = it.hasNext();
		it.close();
		return result;
	}

	/**
	 * In some formulations the context includes deductions that are not yet visible
	 * to the underlying graph but need to be checked for. However, currently this
	 * calls the graph find directly.
	 */
	@Override
	public ClosableIterator<Triple> find(Node s, Node p, Node o) {
//		return graph.findDataMatches(s, p, o).andThen(pendingCache.find(s, p, o));
		return graph.findDataMatches(s, p, o);
	}

	public boolean hasNext() {
		return stack.size() > 0;
	}

	/**
	 * Return the next triple to be added to the graph, removing it from the stack.
	 * 
	 * @return the Triple or null if there are no more
	 */
	public Triple getNextTriple() {
		if (stack.size() > 0) {
			Triple t = stack.remove(stack.size() - 1);
			return t;
		} else {
			return null;
		}
	}

	/**
	 * Reset the binding environment back to empty.
	 * 
	 * @param newSize the number of variables needed for processing the new rule
	 */
	public void resetEnv(int newSize) {
		env.reset(newSize);
	}

	/**
	 * Assert a new triple in the deduction graph, bypassing any processing
	 * machinery.
	 */
	@Override
	public void silentAdd(Triple t) {
		((SilentAddI) graph).silentAdd(t);
	}
}
