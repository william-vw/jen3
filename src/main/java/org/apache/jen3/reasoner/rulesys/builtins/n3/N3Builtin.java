package org.apache.jen3.reasoner.rulesys.builtins.n3;

import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_UNBOUND_VARS;
import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_WRONG_INPUT;

import java.util.stream.Collectors;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.n3.N3MistakeTypes;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet.EvaluationResult;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet.FlowPatternTripleSet;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition.CheckInputResult;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.FlowPattern;

public abstract class N3Builtin {

	protected enum Wrapping {
		NEGATES, INVERSE
	}

	protected BuiltinDefinition definition;
	protected FlowPattern flowPattern;
	protected boolean flowPatternSingleton = false;

	public N3Builtin() {
	}

	public N3Builtin(BuiltinDefinition definition) {
		this.definition = definition;
	}

	public N3Builtin(FlowPattern flowPattern) {
		this.flowPattern = flowPattern;
		flowPattern.setBuiltin(this);
	}

	public N3Builtin(FlowPattern flowPattern, boolean singleton) {
		this(flowPattern);

		this.flowPatternSingleton = singleton;
	}

	public String getURI() {
		return definition.getUri().toString();
	}

	public boolean isResourceIntensive() {
		return definition.isResourceIntensive();
	}

	public boolean isUniversal(Node sb, Node ob) {
		return false;
	}

	public boolean isStatic() {
		return definition.isStatic();
	}

	// returns whether the given input warrants activating the builtin
	// i.e., whether it meets the "domain" and "check-for" restrictions
	// else, the builtin may be false, or not result in binding variables

	public int delayScore(Node s, Node sb, Node o, Node ob, Graph graph) {
		if (definition == null)
			return 0;

		CheckInputResult res = definition.checkInput(s, sb, o, ob, graph);

		switch (res.getClause()) {

		// builtin should not fail just because variables are not yet bound
		// but, if accept failed, this builtin will never succeed
		case ACCEPT:
			// don't delay the builtin assertion; sooner rule fails, the better
			if (!res.isSuccess())
				return 0;

		case NOT_BOUND:
			// "other" can change as variables are being bound
			// (i.e., domain could still be satisfied)
			// so delay these as long as possible
			return -3;

		case DOMAIN:
			// as said, domain can change as variables are bound
			// so, if it did not succeed, delay as long as possible
			if (!res.isSuccess())
				return -3;

			else {
				// push resource-intensive builtins to end of check-for queue
				// (i.e., set of builtins that passed check-for)
				if (definition.isResourceIntensive())
					return -1;

				// delay these builtins, but not as much as ones that don't yet have all
				// their variables bound (but, more than "resource-intensive" ones)
				else if (isUniversal(sb, ob))
					return -2;
			}
			break;

		default:
			break;
		}

		// don't delay (i.e., don't bias the sorting)
		return 0;
	}

	// to be overwritten by "n-ary" triple-set builtins
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		if (flowPatternSingleton)
			return new FlowPatternTripleSet(clause, flowPattern, definition, graph);
		else
			throw new RuntimeException("builtin " + getURI()
					+ " is not a singleton and does not override toTripleSet method");
	}

	public BuiltinDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(BuiltinDefinition definition) {
		this.definition = definition;
	}

	public static interface CheckInputListener {

		public void inputResult(String uri, Node s, Node sb, Node o, Node ob,
				CheckInputResult result);
	}

	public static interface BuiltinEvaluationListener {

		public void evaluationResult(String uri, Node s, Node sb, Node o, Node ob,
				EvaluationResult result);
	}

	public static class BuiltinInputLogger implements CheckInputListener {

		private N3ModelSpec spec;

		public BuiltinInputLogger(N3ModelSpec spec) {
			this.spec = spec;
		}

		@Override
		public void inputResult(String uri, Node s, Node sb, Node o, Node ob,
				CheckInputResult result) {
			N3MistakeTypes mistake = null;
			switch (result.getClause()) {

			case ACCEPT:
				if (!result.isSuccess())
					mistake = BUILTIN_WRONG_INPUT;
				break;

			default:
				break;
			}

			if (result.fellThru()) {
				mistake = BUILTIN_UNBOUND_VARS;
				result = result.getFallThruCause();
			}

			if (mistake != null && spec.hasFeedbackFor(mistake))
				spec.getFeedback(mistake).doDefaultAction(uri, result.getFailed().stream()
						.map(f -> f.toString()).collect(Collectors.joining("\n")));
		}
	}
}