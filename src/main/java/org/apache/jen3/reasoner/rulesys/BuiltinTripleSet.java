package org.apache.jen3.reasoner.rulesys;

import java.util.Collections;
import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition.CheckInputResult;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICStatementConvert;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.FlowPattern;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jen3.util.iterator.NiceIterator;
import org.apache.jen3.util.iterator.SingletonIterator;
import org.apache.jen3.util.iterator.TripleForMatchIterator;
import org.apache.jen3.util.iterator.WrappedIterator;

public abstract class BuiltinTripleSet extends TripleSet {

	protected static EvaluationResult failed = new EvaluationResult(false);

	protected Graph graph;
	protected BuiltinDefinition definition;

	protected BuiltinConfig builtinConfig;

	public BuiltinTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
		super(clause);

		this.definition = definition;

		this.graph = graph;
		this.builtinConfig = graph.getN3Config().getBuiltinConfig();
	}

	@Override
	public boolean requiresMatching() {
		return false;
	}

	public TripleForMatchIterator triples(BindingStack env) {
		ExtendedIterator<Triple> ret = null;
		
		Node s = clause.getSubject(), o = clause.getObject();
		Node sb = env.ground(s), ob = env.ground(o);

//		Log.info(getClass(), "definition: " + definition);
		if (definition != null) {
			EvaluationResult result = checkInput(s, sb, o, ob, env, graph);

			if (result.doCheck()) {
				ICStatementConvert input = result.getResult().getInput();
				sb = input.getSubject().getNode();
				ob = input.getObject().getNode();

				ret = WrappedIterator.create(builtinTriples(sb, ob, env));

			} else {
				CheckInputResult inputResult = result.getResult();
				if (inputResult.fellThru())
					builtinConfig.getInputListener().inputResult(definition.getUri(), s, sb, o, ob,
							inputResult);

				if (result.isSuccess())
					ret = new SingletonIterator<>(new Triple(sb, clause.getPredicate(), ob));
				else
					ret = NiceIterator.emptyIterator();
			}

		} else
			ret = WrappedIterator.create(builtinTriples(sb, ob, env));
		
		return new TripleForMatchIterator(ret);
	}

	public abstract Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env);

	protected EvaluationResult checkInput(Node s, Node sb, Node o, Node ob, BindingStack env,
			Graph graph) {
		EvaluationResult ret = null;

		CheckInputResult res = definition.checkInput(s, sb, o, ob, builtinConfig.getInputListener(),
				graph);
		switch (res.getClause()) {

		case ACCEPT:
			// accept clause is not met: stop processing rule here
			ret = new EvaluationResult(false, res);
			break;

		case DOMAIN:
			// domain clause was successful: return the result
			ret = new EvaluationResult(true, true, res);
			break;

		case NOT_BOUND:
			// accept was successful, but domain was not
			// return value depends on setup of "other" clause
			ret = new EvaluationResult(res.isSuccess(), res);
			break;

		default:
			break;
		}

		if (builtinConfig.hasEvalListener())
			builtinConfig.getEvalListener().evaluationResult(definition.getUri(), s, sb, o, ob,
					ret);

		return ret;
	}

	@Override
	public String toString() {
		return "<BuiltinTripleSet: " + definition.getUri() + ">";
	}

	public abstract static class SingletonTripleSet extends BuiltinTripleSet {

		public SingletonTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env) {
			Triple t = builtinTriple(sb, ob, env);
			if (t != null)
				return new SingletonIterator<>(t);
			else
				return Collections.emptyIterator();
		}

		protected abstract Triple builtinTriple(Node sb, Node ob, BindingStack env);

		// called by subclasses
		// possibly, to be overwritten by subclasses as well

		protected Triple instantiate() {
			return clause.asTriple();
		}
	}

	public static class FlowPatternTripleSet extends SingletonTripleSet {

		protected FlowPattern flowPattern;

		public FlowPatternTripleSet(TriplePattern clause, FlowPattern flowPattern,
				BuiltinDefinition definition, Graph graph) {

			super(clause, definition, graph);

			this.flowPattern = flowPattern;
		}

		@Override
		public Triple builtinTriple(Node sb, Node ob, BindingStack env) {
			flowPattern.setup(sb, ob, env);

			boolean success = flowPattern.apply(env, graph);
			if (success)
				return instantiate();
			else
				return null;
		}

		@Override
		protected Triple instantiate() {
			return flowPattern.instantiate(clause.getPredicate());
		}
	}

	public static class EvaluationResult {

		private boolean success = false;
		private boolean doCheck = false;
		private CheckInputResult result;

		public EvaluationResult(boolean doCheck) {
			this.success = true;
			this.doCheck = doCheck;
		}

		public EvaluationResult(boolean success, CheckInputResult result) {
			this.success = success;
			this.result = result;
		}

		public EvaluationResult(boolean success, boolean doCheck, CheckInputResult result) {
			this.success = success;
			this.doCheck = doCheck;
			this.result = result;
		}

		public boolean isSuccess() {
			return success;
		}

		public boolean doCheck() {
			return doCheck;
		}

		public CheckInputResult getResult() {
			return result;
		}

		public String toString() {
			return (doCheck ? "CHECK" : (success ? "SUCCESS" : "FAILED"));
			// + (result != null ? "\nreason:\n" + result.toString(true) : "");
		}
	}
}
