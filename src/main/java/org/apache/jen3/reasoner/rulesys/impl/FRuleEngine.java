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

import static org.apache.jen3.n3.N3MistakeTypes.INFER_INFERENCE_FUSE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.impl.BindingsTable;
import org.apache.jen3.n3.impl.N3Rule;
import org.apache.jen3.n3.impl.N3Rule.RuleLogics;
import org.apache.jen3.n3.impl.N3Rule.RuleTags;
import org.apache.jen3.reasoner.DummyFinder;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.ReasonerException;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.Builtin;
import org.apache.jen3.reasoner.rulesys.ClauseEntry;
import org.apache.jen3.reasoner.rulesys.ForwardRuleInfGraphI;
import org.apache.jen3.reasoner.rulesys.Functor;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.util.OneToManyMap;
import org.apache.jen3.util.iterator.ContinuationOnFailureIterator;
import org.apache.jen3.util.iterator.SingletonIterator;
import org.apache.jen3.util.iterator.TripleForMatchIterator;
import org.apache.jen3.util.iterator.WrappedIterator;
import org.apache.jena.atlas.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The processing engine for forward production rules. It needs to reference an
 * enclosing ForwardInfGraphI which holds the raw data and deductions.
 */
public class FRuleEngine implements FRuleEngineI {

	/** The parent InfGraph which is employing this engine instance */
	protected ForwardRuleInfGraphI infGraph;

	/** Set of rules being used */
	protected List<Rule> rules;

	/**
	 * Rules that were *not* inferred during reasoning. Any inferred rules will have
	 * to be reset after a rebind.
	 **/
	protected List<Rule> originalRules;

	/**
	 * Map from predicate node to rule + clause, Node_ANY is used for wildcard
	 * predicates
	 */
	protected OneToManyMap<Node, ClausePointer> clauseIndex = new OneToManyMap<>();

	/** List of predicates used in rules to assist in fast data loading */
	protected Set<Node> predicatesUsed = new HashSet<>();

	/**
	 * Flag, if true then there is a wildcard predicate in the rule set so that
	 * selective insert is not useful
	 */
	protected boolean wildcardRule = false;

	/**
	 * Keeps rules with only N3 builtins in the body - these are not matched to
	 * triples but only apply builtins to constants.
	 */
	protected List<Rule> noDataRules = new ArrayList<>();

	/**
	 * Keeps rules with universals in the body - these need to be treated specially.
	 * 
	 */
	protected List<Rule> universalRules = new ArrayList<>();

	/**
	 * Keeps rules indexed by predicates in the rule head. This is only done for
	 * predicates given in {@link N3ModelSpec#getRederivePredicates()}. It allows
	 * re-running all rules that generate certain inferences.
	 * 
	 */
	protected OneToManyMap<Node, Rule> headIndex = new OneToManyMap<>();

	// whether or not to perform initial ruleset saturation
	private boolean saturate = true;

	/** Set to true to flag that derivations should be logged */
	protected boolean recordDerivations;

	/** performance stats - number of rules passing initial trigger */
	int nRulesTriggered = 0;

	/** performance stats - number of rules fired */
	long nRulesFired = 0;

	/** performance stats - number of rules fired during axiom initialization */
	long nAxiomRulesFired = -1;

	/** True if we have processed the axioms in the rule set */
	boolean processedAxioms = false;

	BuiltinSet builtinSet = null;

	Finder clauseCtu = new DummyFinder();

	protected static Logger logger = LoggerFactory.getLogger(FRuleEngine.class);

//  =======================================================================
//  Constructors

	/**
	 * Constructor.
	 * 
	 * @param parent the F or FB infGraph that it using this engine, the parent
	 *               graph holds the deductions graph and source data.
	 * @param rules  the rule set to be processed
	 */
	public FRuleEngine(ForwardRuleInfGraphI parent, List<Rule> rules) {
		infGraph = parent;
		// filter out backward rules
		this.rules = rules.stream().filter(r -> !r.isBackward()).collect(Collectors.toList());

		builtinSet = infGraph.getN3Config().getBuiltinSet();
	}

	/**
	 * Constructor. Build an empty engine to which rules must be added using
	 * setRuleStore().
	 * 
	 * @param parent the F or FB infGraph that it using this engine, the parent
	 *               graph holds the deductions graph and source data.
	 */
	public FRuleEngine(ForwardRuleInfGraphI parent) {
		infGraph = parent;

		builtinSet = infGraph.getN3Config().getBuiltinSet();
	}

	@Override
	public void setClauseContinuation(Finder continuation) {
		this.clauseCtu = continuation;
	}

//  =======================================================================
//  Control methods

	/**
	 * Process all available data. This should be called once a deductions graph has
	 * be prepared and loaded with any precomputed deductions. It will process the
	 * rule axioms and all relevant existing exiting data entries.
	 * 
	 * @param ignoreBrules set to true if rules written in backward notation should
	 *                     be ignored
	 * @param inserts      the set of triples to be processed, normally this is the
	 *                     raw data graph but may include additional deductions made
	 *                     by preprocessing hooks
	 */
	@Override
	public void init(boolean ignoreBrules, Finder inserts) {
		resetOriginalRules();

		// Create the reasoning context
		// (wvw) use the same one for axioms and fast-init
		// we only want addSet to be fired once, given this cumulative context
		// (universal rules are then fired once, at the end of addSet)
		BFRuleContext context = new BFRuleContext(infGraph);

		findAndProcessAxioms(context);
		nAxiomRulesFired = nRulesFired;
		logger.debug("Axioms fired " + nAxiomRulesFired + " rules");

		fastInit(inserts, context);
	}

	// rebind needs to also reset inferred rules, which are not part of deduction
	// model - so manually reset these here
	private void resetOriginalRules() {
		// there hasn't been a prior init call ; so current rules are the original
		if (originalRules == null) {
			originalRules = new ArrayList<>(rules);
			return;
		}

		Iterator<Rule> it = rules.iterator();
		while (it.hasNext()) {
			Rule rule = it.next();

			// if a rule had been inferred, remove it
			if (!originalRules.contains(rule)) {
				remove(rule, true);
				it.remove();
			}
		}
	}

	/**
	 * Process all available data. This version expects that all the axioms have
	 * already be preprocessed and the clause index already exists.
	 * 
	 * @param inserts the set of triples to be processed, normally this is the raw
	 *                data graph but may include additional deductions made by
	 *                preprocessing hooks
	 */
	@Override
	public void fastInit(Finder inserts) {
		fastInit(inserts, new BFRuleContext(infGraph));
	}

	public void fastInit(Finder inserts, BFRuleContext context) {
		findAndProcessActions();

		if (saturate)
			saturateRuleset(context);

		else {
			collectTriggerTriples(inserts, context);
			triggerRuleset(context);
		}
	}

	private void collectTriggerTriples(Finder inserts, BFRuleContext context) {
		// TODO leverage indexes where possible for s/o
		// (won't be possible if they're aggregates, such as collections or formulas)
		// (same goes for predicates, btw ..)

		// Insert the data
		if (wildcardRule) {
			for (Iterator<Triple> i = inserts.find(new TriplePattern(null, null, null)); i
					.hasNext();) {
				context.addTriple(i.next());
			}
		} else {
			for (Node predicate : predicatesUsed) {
				for (Iterator<Triple> i = inserts.find(new TriplePattern(null, predicate, null)); i
						.hasNext();) {
					context.addTriple(i.next());
				}
			}
		}
	}

	/**
	 * Process the subset of data relevant to a (new) rule. This version expects
	 * that all the axioms have already be preprocessed and the clause index already
	 * exists.
	 */
	public void fastInit(Rule r) {
		// Log.info(getClass(), "fastInit: " + r);

		findAndProcessActions();
		// create the reasoning context
		BFRuleContext context = new BFRuleContext(infGraph);

		r.forEachInBody((clause, index) -> {
			if (isTriplePattern(clause)) {
				TriplePattern tp = (TriplePattern) clause;
				Node predicate = tp.getPredicate();

				// infGraph; need to include inferred triples as well
				if (predicate.isVariable())
					infGraph.find(null, null, null).forEachRemaining(t -> context.addTriple(t));
				else
					infGraph.find(null, predicate, null)
							.forEachRemaining(t -> context.addTriple(t));
			}
		});

		// run the engine

		// dealing with an inferred rule
		if (curContext != null) {
			context.setParent(curContext);
			curContext = context;

			processInferredRule(context, r);

		} else
			// dealing with a rule added via the API
			processRuleDirectly(context, r);
	}

	@Override
	public void deriveFor(Node... predicates) {
		for (Node predicate : predicates) {

			headIndex.getAll(predicate).forEachRemaining(r -> {
				// Log.info(getClass(), "re-deriving: " + r);
				fastInit(r);
			});
		}
	}

	public Collection<Rule> getRules() {
		return rules;
	}

	public void addRule(Rule rule, boolean ignoreBrules) {
		if ((!rule.isBackward() || !ignoreBrules) && !rules.contains(rule)) {
			compile(rule, ignoreBrules);
			rules.add(rule);
		}
	}

	public boolean removeRule(Rule rule, boolean ignoreBrules) {
		if ((!rule.isBackward() || !ignoreBrules) && rules.remove(rule)) {
			remove(rule, ignoreBrules);
			return true;

		} else
			return false;
	}

	/**
	 * Add one triple to the data graph, run any rules triggered by the new data
	 * item, recursively adding any generated triples.
	 */
	@Override
	public synchronized void add(Triple t) {
		// Log.info(getClass(), "add: " + t);

		BFRuleContext context = new BFRuleContext(infGraph);
		context.addTriple(t);

		triggerRuleset(context);
	}

	/**
	 * Remove one triple to the data graph.
	 * 
	 * @return true if the effects could be correctly propagated or false if not (in
	 *         which case the entire engine should be restarted).
	 */
	@Override
	public synchronized boolean delete(Triple t) {
		// Incremental delete not supported
		return false;
	}

	/**
	 * Return the number of rules fired since this rule engine instance was created
	 * and initialized
	 */
	@Override
	public long getNRulesFired() {
		return nRulesFired;
	}

	/**
	 * Return true if the internal engine state means that tracing is worthwhile. It
	 * will return false during the axiom bootstrap phase.
	 */
	@Override
	public boolean shouldTrace() {
//        return processedAxioms;
		return true;
	}

	/**
	 * Set to true to enable derivation caching
	 */
	@Override
	public void setDerivationLogging(boolean recordDerivations) {
		this.recordDerivations = recordDerivations;
	}

	/**
	 * Access the precomputed internal rule form. Used when precomputing the
	 * internal axiom closures.
	 */
	@Override
	public Object getRuleStore() {
		return new RuleStore(clauseIndex, predicatesUsed, wildcardRule, headIndex);
	}

	/**
	 * Set the internal rule from a precomputed state.
	 */
	@Override
	public void setRuleStore(Object ruleStore) {
		RuleStore rs = (RuleStore) ruleStore;
		clauseIndex = rs.clauseIndex;
		predicatesUsed = rs.predicatesUsed;
		wildcardRule = rs.wildcardRule;
		headIndex = rs.headIndex;
	}

//  =======================================================================
//  Internal methods

	/*
	 * Process a rule added via the API, after deductions were derived. Need to
	 * conduct a regular inference run for this rule.
	 * 
	 * First, the context stack will contain only triples relevant to the new rule;
	 * as conclusions are being inferred for this rule, these will be added to the
	 * stack, possibly leading to other rules being fired.
	 */

	private void processRuleDirectly(BFRuleContext context, Rule rule) {
		// will always be the top level
		// (other levels are introduced by inferred rules; see #processInferredRule)
		curContext = context;

		// if it was a no-data rule, it will be the only rule fired here
		// (since the prior ones will have been removed after being fired)
		runNoDataRules(context);

		while (context.hasNext()) {
			// Log.info(getClass(), "\n>> (rule.new) inference run\n");

			while (context.hasNext()) {
				Triple t = context.getNextTriple();
				// Log.info(getClass(), "(rule.new) nextTriple? " + t + "\n(for " + rule + ")");

				// Check for rule triggers
				Iterator<ClausePointer> i = getClausePointers(t, rule);
				while (i.hasNext()) {
					ClausePointer cp = i.next();

					tryRule(context, t, cp);
				}
			}
		}

		tryUniversalRules(context);

		curContext = null;
	}

	/*
	 * Process a rule that was inferred while deriving deductions.
	 * 
	 * We only need to get our engine up to speed with this rule - inferring any
	 * conclusions based on relevant triples, and adding them to the top-level
	 * context (i.e., context belonging to the original run that inferred the rule).
	 * 
	 * Afterwards, the rule will have been added to the index, and any rule
	 * conclusions are on the (top-level) context stack where they can trigger other
	 * rules.
	 */
	private void processInferredRule(BFRuleContext context, Rule rule) {
		while (context.hasNext()) {
			Triple t = context.getNextTriple();
			// Log.info(getClass(), "(rule.inf) nextTriple? " + t + "\n(for " + rule + ")");

			// Check for rule triggers
			Iterator<ClausePointer> i = getClausePointers(t, rule);
			while (i.hasNext()) {
				ClausePointer cp = i.next();

				tryRule(context, t, cp);
			}
		}
	}

	private BFRuleContext curContext = null;
	private boolean currentlySaturating = false;

	/**
	 * Add a set of new triples to the data graph, run any rules triggered by the
	 * new data item, recursively adding any generated triples. Technically the
	 * triples having been physically added to either the base or deduction graphs
	 * and the job of this function is just to process the stack of additions firing
	 * any relevant rules.
	 * 
	 * @param context a context containing a set of new triples to be added
	 */
	protected void triggerRuleset(BFRuleContext context) {
		currentlySaturating = false;

		curContext = context;

		runNoDataRules(context);

		// in case no relevant triples were found for (non-universal) rules
		// (or, there are no non-universal rules)
		if (!context.hasNext()) {
			// Log.info(getClass(), "\n>> (all.trigger) inference run\n");
			tryUniversalRules(context);
		}

		while (context.hasNext()) {
			// Log.info(getClass(), "\n>> (all.trigger) inference run\n");

			while (context.hasNext()) {
				Triple t = context.getNextTriple();
				// Log.info(getClass(), "(all.trigger) nextTriple? " + t);

				// Check for rule triggers
				Set<Rule> firedRules = new HashSet<>();

				Iterator<ClausePointer> i = getClausePointers(t);
				while (i.hasNext()) {
					ClausePointer cp = i.next();
					// Log.info(getClass(), "cp.rule? " + cp);

					if (firedRules.contains(cp.rule))
						continue;

					if (tryRule(context, t, cp))
						firedRules.add(cp.rule);
				}
			}
			// at this point, non-universal rules are all fired-out
			// let's try our universal rules
			tryUniversalRules(context);

			// as a result of universal-rules, new triples may have been generated
			// so let's start this outer loop again

			// TODO likely a good idea to do infinite-loop checking here
		}

		curContext = null;
	}

	/**
	 * For each rule, iterate over rule clauses, joining any resulting triples until
	 * all clauses have been matched, leading to one or more inferences.
	 * 
	 * The goal of this method is to avoid the same inference being generated
	 * multiple times, due to the relevant triples, i.e., each involved in the
	 * inference, individually "triggering" the rule to fire, repeating the same
	 * joins. In practice this is a pitfall of the
	 * {@link #triggerRuleset(BFRuleContext)} method. Currently we only use the
	 * latter method for processing generated inferences; the assumption is that
	 * there will be much fewer inferences than initial data items.
	 * 
	 * 
	 * @param context a context containing a set of new triples to be added
	 */
	protected void saturateRuleset(BFRuleContext context) {
		currentlySaturating = true;

		curContext = context;

		runNoDataRules(context);

		// Log.info(getClass(), "\n>> (all.saturate) inference run\n");
		for (Rule r : rules) {
			N3Rule n3r = (N3Rule) r;
			// no-data or universal rule
			if (n3r.hasTag())
				continue;

			// Log.info(getClass(), "(all.saturate) nextRule? " + r);

			context.resetEnv(r.getNumVars());
			context.setRule(r);

			// any inferred triples are added as pending
			matchRuleBody(-1, context);
		}

		// (only try universal rules after all non-universal rules are fired out)

		// Log.info(getClass(), "\n>> done saturating");

		currentlySaturating = false;

		// flush all pending deductions at this point
		// this will add them to the context stack and deductions graph
		context.flushPending();

		// only in case "regular" rules are all fired-out, try our universal rules
		// (these should always be fired at the end of a reasoning run!)
		if (!context.hasNext()) {
			tryUniversalRules(context);
		}

		// in case "regular" rules weren't fired out, or, if they were,
		// the universal rules fired some inferences
		while (context.hasNext()) {
			// fire rule clauses that match the inferred triples
			processTriggerClauses(context);

			// at this point, "regular" rules are all fired-out
			// let's try our universal rules
			tryUniversalRules(context);

			// as a result of universal-rules, new triples may have been generated
			// so let's start the outer loop again

			// TODO likely a good idea to do infinite-loop checking here
		}

		curContext = null;
	}

	protected void processTriggerClauses(BFRuleContext context) {
		while (context.hasNext()) {
			Triple t = context.getNextTriple();
			// Log.info(getClass(), "(trigger) nextTriple? " + t);

			// Check for rule triggers
			Set<Rule> firedRules = new HashSet<>();

			Iterator<ClausePointer> i = getClausePointers(t);
			while (i.hasNext()) {
				ClausePointer cp = i.next();
				// Log.info(getClass(), "cp.rule? " + cp);

				// don't fire rule multiple times for same triple
				if (firedRules.contains(cp.rule))
					continue;

				if (tryRule(context, t, cp))
					firedRules.add(cp.rule);
			}
		}
	}

	private Iterator<ClausePointer> getClausePointers(Triple t) {
		Iterator<ClausePointer> i = null;
		if (t.getPredicate().isVariable())
			i = clauseIndex.values(true).iterator();
		else {
			// (true param in calls; it's possible that a rule infers another rule,
			// triggering an update of clause index; which causes a conc mod exc)
			Iterator<ClausePointer> i1 = clauseIndex.getAll(t.getPredicate(), true);
			Iterator<ClausePointer> i2 = clauseIndex.getAll(Node.ANY, true);
			i = WrappedIterator.create(i1).andThen(i2);
		}
		return i;
	}

	private Iterator<ClausePointer> getClausePointers(Triple t, Rule r) {
		List<ClausePointer> ret = new ArrayList<>();

		r.forEachInBody((clause, index) -> {
			if (isTriplePattern(clause)) {
				TriplePattern tp = (TriplePattern) clause;

				if (t.getPredicate().isVariable() || tp.getPredicate().isVariable()
						|| t.getPredicate().equals(tp.getPredicate()))

					ret.add(new ClausePointer(r, index));
			}
		});

		return ret.iterator();
	}

	private boolean tryRule(BFRuleContext context, Triple t, ClausePointer cp) {
		context.resetEnv(cp.rule.getNumVars());
		TriplePattern trigger = (TriplePattern) cp.rule.getBodyElement(cp.index);

		// Log.info(getClass(), "trigger clause? " + trigger);
		// Log.info(getClass(), "trigger triple? " + t);

		context.setRule(cp.rule);

		// TODO this will not work in case of an improved indexing of rules
		// where clauses are indexed on more than predicate
		// in that case, no opportunity to try continuation here

		TripleForMatchIterator it = new ContinuationOnFailureIterator(
				new SingletonIterator<Triple>(t), (v) -> clauseCtu.find(trigger));

		boolean foundMatch = false;

		BindingStack env = context.getEnvStack();
		while (it.hasNext()) {
			Triple t2 = it.next();
			// Log.info(getClass(), "trying trigger: " + t2);

			env.push();
			if (RuleUtil.match(trigger, t2, env)) {
				nRulesTriggered++;

				if (matchRuleBody(cp.index, context)) {
					nRulesFired++;
					foundMatch = true;
				}
			}
			env.unwind();

			it.foundMatch(foundMatch);
		}
		
		it.close();

		return foundMatch;
	}

	// check builtin-only (i.e., no-data) rules separately;
	// these won't match any new triples
	private void runNoDataRules(BFRuleContext context) {
		// oneShot = true
		// only fire data-only rules once
		// (unaffected by any new triples that may be added)
		runRules(noDataRules, context, true);
	}

	private boolean tryUniversalRules(BFRuleContext context) {
		// oneShot = false
		// universals need to be run each time new data is added or inferred
		return runRules(universalRules, context, false);
	}

	private boolean runRules(List<Rule> rules, BFRuleContext context, boolean oneShot) {
		// Log.info(getClass(), "\nrunning special rules: oneShot? " + oneShot);

		// avoid concurrent modification errors here
		for (int i = 0; i < rules.size(); i++) {
			Rule r = rules.get(i);
			if (oneShot)
				rules.remove(i--);

			context.resetEnv(r.getNumVars());
			context.setRule(r);
			// will run triple patterns on any relevant triples in the graph
			matchRuleBody(-1, context);
		}

		// return whether any new triples were inferred
		return context.hasNext();
	}

	/**
	 * Match the rest of a set of rule clauses once an initial rule trigger has
	 * fired. Carries out any actions as a side effect.
	 * 
	 * @param trigger the index of the clause which has already be successfully
	 *                matched
	 * @param context a context containing a set of new triples to be added
	 * @return true if the rule actually fires
	 */
	private boolean matchRuleBody(int trigger, BFRuleContext context) {
		Rule rule = context.getRule();
		List<TriplePattern> clauses = getClauses(rule.getBody(), trigger);

		boolean matched = matchClauseList(clauses, context);
		if (!currentlySaturating && matched) {
			// new deductions are stashed and we're not saturating:
			// assert these as deductions and add them to processing stack
			context.flushPending();
			// Log.info(getClass(), "flushing context");
		}
		return matched;
	}

	private List<TriplePattern> getClauses(ClauseEntry[] array, int skip) {
		List<TriplePattern> clauses = new ArrayList<>();
		for (int i = 0; i < array.length; i++) {
			if (i == skip)
				continue;
			if (isTripleSet(array[i]))
				clauses.add((TriplePattern) array[i]);
		}

		return clauses;
	}

	protected void compile(Rule r, boolean ignoreBrules) {
		updateRules(r, ignoreBrules, (p, cp) -> {
			if (p.isVariable()) {
				clauseIndex.put(Node.ANY, cp);
				wildcardRule = true;

			} else {
				clauseIndex.put(p, cp);
				// (wvw) avoid inconsistent predicateUsed set
				// (needed wildcard rule can be removed afterwards now)
				// if ( !wildcardRule )
				// {
				predicatesUsed.add(p);
				// }
			}

		}, (u) -> universalRules.add(u), (n) -> noDataRules.add(n),
				(p, rule) -> headIndex.put(p, rule));
	}

	// TODO utilize tags to remove from appropriate data structures
	protected void remove(Rule r, boolean ignoreBrules) {
		updateRules(r, ignoreBrules, (p, cp) -> {
			if (p.isVariable()) {
				clauseIndex.remove(Node.ANY, cp);
				// still contains a wildcard after removal?
				wildcardRule = clauseIndex.containsKey(Node.ANY);

			} else {
				clauseIndex.remove(p, cp);
				// predicate could still be in 1-many map
				// (alternative could be to have a cnt per predicate)
				predicatesUsed = clauseIndex.keySet().stream().filter(k -> k != Node.ANY)
						.collect(Collectors.toSet());
			}

		}, (u) -> universalRules.remove(u), (n) -> noDataRules.remove(n),
				(p, rule) -> headIndex.remove(p, rule));
	}

	protected void updateRules(Rule r, boolean ignoreBrules,
			BiConsumer<Node, ClausePointer> updateBodyIndx, Consumer<Rule> univ,
			Consumer<Rule> noData, BiConsumer<Node, Rule> updateHeadIdx) {

		if (ignoreBrules && r.isBackward())
			return;

		Object[] body = r.getBody();

		boolean noDataRule = true;
		boolean universalRule = false;

		for (int i = 0; i < body.length; i++) {
			TriplePattern tp = (TriplePattern) body[i];
			if (isBuiltinPattern(tp)) {
				if (isUniversalBuiltin(tp))
					universalRule = true;
			} else
				noDataRule = false;
		}

		if (universalRule) {
			((N3Rule) r).setTag(RuleTags.HAS_UNIV);
			univ.accept(r);
		}

		else if (noDataRule) {
			((N3Rule) r).setTag(RuleTags.NO_DATA);
			noData.accept(r);
		}

		else {
			for (int i = 0; i < body.length; i++) {
				TriplePattern tp = (TriplePattern) body[i];

				if (!isBuiltinPattern(tp)) {
					Node predicate = tp.getPredicate();
					ClausePointer cp = new ClausePointer(r, i);

					updateBodyIndx.accept(predicate, cp);
				}
			}
		}

		N3ModelSpec spec = ((N3Rule) r).getModelSpec();
		if (spec.allowRederives()) {
			r.forEachInHead(cl -> {
				TriplePattern tp = (TriplePattern) cl;

				if (spec.getRederivePredicates().contains(tp.getPredicate()))
					updateHeadIdx.accept(tp.getPredicate(), r);
			});
		}
	}

	/**
	 * Scan the rules for any axioms and insert those
	 */
	protected void findAndProcessAxioms(BFRuleContext context) {
		for (Rule r : rules) {
			if (r.bodyLength() == 0) {
				// An axiom
				for (int j = 0; j < r.headLength(); j++) {
					Object head = r.getHeadElement(j);
					if (head instanceof TriplePattern) {
						TriplePattern h = (TriplePattern) head;
						Triple t = new Triple(h.getSubject(), h.getPredicate(), h.getObject());
						context.addTriple(t);
						infGraph.getDeductionsGraph().add(t);
					}
				}
			}
		}
		processedAxioms = true;
	}

	/**
	 * Scan the rules for any actions and run those
	 */
	protected void findAndProcessActions() {
		BFRuleContext context = new BFRuleContext(infGraph);
		for (Rule r : rules) {
			if (r.bodyLength() == 0) {
				// An axiom
				for (int j = 0; j < r.headLength(); j++) {
					Object head = r.getHeadElement(j);
					if (head instanceof Functor) {
						Functor f = (Functor) head;
						Builtin imp = f.getImplementor();
						if (imp != null) {
							context.setRule(r);
							imp.headAction(f.getArgs(), f.getArgLength(), context);
						} else {
							throw new ReasonerException("Invoking undefined Functor " + f.getName()
									+ " in " + r.toShortString());
						}
					}
				}
			}
		}
	}

	private boolean isTripleSet(Object clause) {
		return (clause instanceof TriplePattern);
	}

	private boolean isTriplePattern(Object clause) {
		return (clause instanceof TriplePattern)
				&& !builtinSet.isBuiltin(((TriplePattern) clause).getPredicate());
	}

	private boolean isBuiltinPattern(TriplePattern clause) {
		return builtinSet.isBuiltin(clause.getPredicate());
	}

	private boolean isUniversalBuiltin(TriplePattern c) {
		if (builtinSet.isBuiltin(c.getPredicate())) {
			N3Builtin builtin = builtinSet.getBuiltin(c.getPredicate());
			// base this on original (compile-time) constants in the triple pattern
			return builtin.isUniversal(c.getSubject(), c.getObject());

		} else
			return false;
	}

	/**
	 * Match each of a list of clauses in turn. For all bindings for which all
	 * clauses match check the remaining clause guards and fire the rule actions.
	 * 
	 * @param clauses the list of clauses to match, start with last clause
	 * @param context a context containing a set of new triples to be added
	 * @return true if the rule actually fires
	 */
	private boolean matchClauseList(List<TriplePattern> clauses, BFRuleContext context) {
		BindingStack env = context.getEnvStack();
		if (clauses.isEmpty())
			return fireRule(context);

		List<TriplePattern> sorted = ClauseSort.sortOnBoundedness(clauses, env, infGraph);

		// Log.info(getClass(), "sorted: " + sorted);

		TriplePattern clause = sorted.remove(0);
		// Log.info(getClass(), "clause? " + clause + " - " + env);

		// hack to avoid resource-intensive builtins to be fired more than once..
		// (e.g., log:content, log:semantics)
		// requires the rule to have a "bindings-table" to keep bindings that have
		// already been tried
		// (these builtins are indicated as "isResourceIntensive"

		if (alreadyDone(clause, env))
			return false;

		TripleSet set = RuleUtil.createTripleSet(clause, env, infGraph, clauseCtu);
		TripleForMatchIterator it = set.triples(env);
		// Log.info(getClass(), "set.triples? " + set + ", " + set.getClass() + " - " +
		// clause + " - "
		// + it.hasNext());

		boolean foundMatch = false;

		while (it.hasNext()) {
			Triple t = it.next();

			// Add the bindings to the environment
			env.push();
			// (e.g., most builtin triple-sets don't require matching again)
			boolean bound = RuleUtil.match(clause, t, !set.requiresMatching(), env);
			if (bound) {
				// Log.info(getClass(), "env after binding: " + env);
				foundMatch |= matchClauseList(sorted, context);
			}
			env.unwind();
			
			it.foundMatch(foundMatch); // let iterator know if we found a match
		}
//		if (foundMatch)
//			Log.debug(getClass(), "found! " + clause);

		it.close();
		
		return foundMatch;
	}

	private boolean alreadyDone(TriplePattern clause, BindingStack env) {
		N3Rule rule = (N3Rule) env.getRule();

		if (rule.hasBindingsTable()) {
			Node predicate = clause.getPredicate();

			if (builtinSet.isBuiltin(predicate)
					&& builtinSet.getBuiltin(predicate).isResourceIntensive()) {
				BindingsTable table = rule.getBindingsTable();
				Log.debug(getClass(), "table? " + table + "\nenv? " + env);

				if (table.checkAndAdd(env)) {
					// Log.info(getClass(), "already done: clause? " + clause + " - env? " + env);
					return true;
				}
			}
		}

		return false;
	}

	private boolean fireRule(BFRuleContext context) {
		N3Rule rule = (N3Rule) context.getRule();
		BindingStack env = context.getEnvStack();

		N3Rule n3Rule = (N3Rule) rule;

		List<Triple> triples = null;

		Node head = n3Rule.getHeadNode();
		if (head.isVariable()) {
			Node_RuleVariable headVar = (Node_RuleVariable) head;

			Node binding = env.getBinding(headVar);
			if (binding != null) {
				head = binding;

				// {..} => ?x (x bound to cited-formula)
				if (head.isCitedFormula())
					triples = env.instantiateOutput((Node_CitedFormula) head);
			}
		}

		if (Util.isBoolean(head)) {
			// {..} => false (inference fuse)
			if (!Util.getBooleanValue(head))
				n3Rule.getModelSpec().getFeedback(INFER_INFERENCE_FUSE).doDefaultAction(rule);

			return false;
		}

		// "regular" case
		if (triples == null)
			triples = env.instantiateOutput(rule);

		// Log.info(getClass(), "\nrule: " + rule);
		// Log.info(getClass(), "instantiated:\n" + triples);
		// Log.info(getClass(), "env:\n" + env);

		boolean anyRemoved = false;

		// support for linear logic
		if (rule.getLogic() == RuleLogics.LINEAR) {
			for (ClauseEntry cl : rule.getBody()) {
				TriplePattern tp = (TriplePattern) cl;

				// if a tp is not tagged as stable truth, remove it from the graph here
				// .. this will end in tears if people aren't careful
				if (!tp.isStableTruth()) {
					// instantiate triple pattern
					Triple t = env.instantiate(tp);
//					// Log.info(getClass(), "removing.t? " + t);

					anyRemoved |= context.removeAndCheck(t);
				}
			}

			/*
			 * None of the triples ended up being removed (i.e., queued up for removal).
			 * This is possible when a number of different triples yield a binding
			 * environment that results in the same triples to be retracted.
			 * 
			 * Triples are not directly retracted (rather, queued up) - it would be a huge
			 * hassle to dynamically update all iterators (e.g., context, triple sets).
			 */
			// Log.info(getClass(), "removed any triple? " + anyRemoved);
			if (!anyRemoved)
				return false;
		}

//		boolean assertTriples = (rule.getLogic() == RuleLogics.LINEAR);

		boolean added = false;
		for (Triple t : triples) {

			if (!context.contains(t)) {
				if (anyRemoved) {
//					// Log.info(getClass(), "adding.t? " + t);
				}
				added = true;
				context.add(t);
			}
		}

		return added;
	}

//    /**
//     * Instantiate a triple pattern against the current environment.
//     * This version handles unbound varibles by turning them into bNodes.
//     * @param clause the triple pattern to match
//     * @param env the current binding environment
//     * @return a new, instantiated triple
//     */
//    public static Triple instantiate(TriplePattern pattern, BindingStack env) {
//        Node s = env.getBinding(pattern.getSubject());
//        if (s == null) s = Node.createAnon();
//        Node p = env.getBinding(pattern.getPredicate());
//        if (p == null) p = Node.createAnon();
//        Node o = env.getBinding(pattern.getObject());
//        if (o == null) o = Node.createAnon();
//        return new Triple(s, p, o);
//    }

//=======================================================================
// Inner classes

	/**
	 * Structure used in the clause index to indicate a particular clause in a rule.
	 * This is used purely as an internal data structure so we just use direct field
	 * access.
	 */
	protected static class ClausePointer {

		/** The rule containing this clause */
		protected Rule rule;

		/** The index of the clause in the rule body */
		protected int index;

		/** constructor */
		ClausePointer(Rule rule, int index) {
			this.rule = rule;
			this.index = index;
		}

		/** Get the clause pointed to */
		TriplePattern getClause() {
			return (TriplePattern) rule.getBodyElement(index);
		}

		public String toString() {
			return "[@" + index + "] " + rule;
		}
	}

	/**
	 * Structure used to wrap up processed rule indexes.
	 */
	public static class RuleStore {

		/**
		 * Map from predicate node to rule + clause, Node_ANY is used for wildcard
		 * predicates
		 */
		protected OneToManyMap<Node, ClausePointer> clauseIndex;

		/** List of predicates used in rules to assist in fast data loading */
		protected Set<Node> predicatesUsed;

		/**
		 * Flag, if true then there is a wildcard predicate in the rule set so that
		 * selective insert is not useful
		 */
		protected boolean wildcardRule;

		protected OneToManyMap<Node, Rule> headIndex;

		/** Constructor */
		RuleStore(OneToManyMap<Node, ClausePointer> clauseIndex, Set<Node> predicatesUsed,
				boolean wildcardRule, OneToManyMap<Node, Rule> headIndex) {
			this.clauseIndex = clauseIndex;
			this.predicatesUsed = predicatesUsed;
			this.wildcardRule = wildcardRule;
			this.headIndex = headIndex;
		}
	}
}
