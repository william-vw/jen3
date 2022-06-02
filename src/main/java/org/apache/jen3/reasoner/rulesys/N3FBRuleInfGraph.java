package org.apache.jen3.reasoner.rulesys;

import java.util.ArrayList;
import java.util.List;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.Reasoner;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.impl.LPBRuleEngine;
import org.apache.jen3.reasoner.rulesys.impl.LPRuleStore;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jen3.util.iterator.NiceIterator;

public class N3FBRuleInfGraph extends BasicForwardRuleInfGraph implements BackwardRuleInfGraphI {

	protected LPBRuleEngine bEngine;
	protected LPRuleStore bRuleStore;

	protected List<Rule> rules;
	protected List<Rule> originalRules;

	protected Finder lpCtu = new LPEngineContinuation();

	public N3FBRuleInfGraph(Reasoner reasoner, Graph schema) {
		super(reasoner, schema);

		init();
	}

	public N3FBRuleInfGraph(Reasoner reasoner, List<Rule> rules, Graph schema, GraphConfig config) {
		super(reasoner, rules, schema, config);

		this.rules = rules;

		init();
	}

	public N3FBRuleInfGraph(Reasoner reasoner, List<Rule> rules, Graph schema, Graph data,
			GraphConfig config) {

		super(reasoner, rules, schema, data, config);

		this.rules = rules;

		init();
	}

	private void init() {
		bEngine = new LPBRuleEngine(this);
		// (will be loaded with backward-rules during prepare())
		bRuleStore = bEngine.getRuleStore();

		engine.setClauseContinuation(lpCtu);
	}

	// -- START BasicForwardRuleInfGraph

	@Override
	public void addRule(Rule rule) {
		if (rule.isBackward()) {
//			Log.info(getClass(), "adding brule: " + rule);
			bEngine.addRule(rule);
			bEngine.reset();

		} else {
//			Log.info(getClass(), "adding frule: " + rule);
			super.addRule(rule);
		}

		rules.add(rule);
	}

	@Override
	public boolean removeRule(Rule rule) {
		boolean ret = false;

		if (rule.isBackward()) {
			bEngine.deleteRule(rule);
			bEngine.reset();

			ret = true;

		} else
			ret = super.removeRule(rule);

		rules.remove(rule);

		return ret;
	}

	@Override
	public synchronized void prepare() {
		if (this.isPrepared())
			return;

		// at this point, assume all rules were added via API
		// (in case of N3, from the data via N3ModelImpl)
		// (i.e., rules added from now on will be inferred)
		if (originalRules == null)
			originalRules = new ArrayList<>(rules);

		// reset to original set of rules (i.e., non-inferred)
		bEngine.deleteAllRules();
		originalRules.stream().filter(r -> r.isBackward()).forEach(r -> bEngine.addRule(r));

		// (not currently needed; rules is only used for keeping originalRules)
		rules = new ArrayList<>(originalRules);

		// (FRuleEngine will similarly reset to its original rules)
		// (will also set prepared-state to true)
		super.prepare();
	}

	@Override
	public void rebind(Graph data) {
		super.rebind(data);

		bEngine.reset();
	}

	@Override
	public void rebind(boolean resetDeductions) {
		super.rebind(resetDeductions);

		bEngine.reset();
	}

	// -- END BasicForwardRuleInfGraph

	// -- START BackwardRuleInfGraphI

	// seems to only pertain to jena builtins, which aren't used here anyway

	// (doesn't seem to be called)
	@Override
	public boolean processBuiltin(ClauseEntry clause, Rule rule, BindingEnvironment env) {
		return false;
	}

	@Override
	public ExtendedIterator<Triple> findDataMatches(TriplePattern pattern) {
		return fdata.find(pattern).andThen(fdeductions.find(pattern));
	}

	@Override
	public Node getTemp(Node instance, Node prop, Node pclass) {
		return null;
	}

	// -- END BackwardRuleInfGraphI

	public static int ctuCnt = 0;

	private class LPEngineContinuation implements Finder {

		@Override
		public ExtendedIterator<Triple> find(TriplePattern pattern) {
			bRuleStore.initialize();

			if (pattern.getPredicate().isConcrete() && pattern.getObject().isConcrete()
					&& bRuleStore.isIndexed(pattern.getPredicate(), pattern.getObject())) {

				ctuCnt++;

				ExtendedIterator<Triple> it = bEngine.find(pattern);
//				Log.info(getClass(), "ctu.find: " + pattern);
//				Log.info(getClass(), "next? " + it.hasNext());
				return it;

			} else
				return NiceIterator.emptyIterator();
		}

		@Override
		public ExtendedIterator<Triple> findWithContinuation(TriplePattern pattern,
				Finder continuation) {
			return find(pattern).andThen(continuation.find(pattern));
		}

		@Override
		public boolean contains(TriplePattern pattern) {
			throw new UnsupportedOperationException();
//			return false;
		}
	}
}
