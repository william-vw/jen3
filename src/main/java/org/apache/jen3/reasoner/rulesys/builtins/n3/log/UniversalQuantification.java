package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.List;
import java.util.function.Function;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.InfGraph;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet.SingletonTripleSet;
import org.apache.jen3.reasoner.rulesys.RuleContext;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.ClauseSort;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;
import org.apache.jen3.util.iterator.TripleForMatchIterator;

public abstract class UniversalQuantification extends LogBuiltin {

	protected enum MatchGoals {

		NO_NEG, MIN_1_POS
	}

	@Override
	public boolean isUniversal(Node sb, Node ob) {
		// if object refers to the base IRI, then their contents will be the
		// entire current document (= universal builtin)

		// for compatibility, object may also be a variable

		return ob.isVariable() || ob.isURI();
	}

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new UnivTripleSet(clause, definition, graph, continuation);
	}

	protected abstract boolean match(Node sb, Node ob, BindingStack env, Graph graph,
			Finder continuation);

	protected Graph getTargetGraph(Node ob, Graph graph) {
		if (ob.isCitedFormula())
			return ((Node_CitedFormula) ob).getContents().getGraph();
		else
			// in case object is variable or base IRI
			return graph;
	}

	private class UnivTripleSet extends SingletonTripleSet {

		private Finder continuation;

		public UnivTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph,
				Finder continuation) {

			super(clause, definition, graph);

			this.continuation = continuation;
		}

		@Override
		protected Triple builtinTriple(Node sb, Node ob, BindingStack env) {
			if (match(sb, ob, env, graph, continuation))
				return instantiate();
			else
				return null;
		}
	}

	// code below is mostly based on FRuleEngine

	/**
	 * This process will try to match the clauses in the given {@link MatchContext}
	 * (incl. pattern and builtin clauses) with the given {@link InfGraph},
	 * returning true/false depending on whether it succeeded.
	 * 
	 * A coherent match includes a binding environment that is consistent for all
	 * given clauses. A match can be either positive or negative depending on the
	 * checkMatch function: this function is used by forAllIn and collectAllIn. The
	 * includes builtin will always consider a coherent match as positive.
	 * 
	 * NO_NEG: the process returns false once a negative match is found. If no
	 * matches are found, the process returns true. Geared towards the body part of
	 * forAllIn, and collectAllIn.
	 * 
	 * MIN_1_POS: the process returns true once a positive example is found. If no
	 * matches are found, the process returns false. Geared towards the head part of
	 * forAllIn.
	 * 
	 * ALL_MATCH: a combination of the two above. The process returns false once a
	 * negative match is found. If no matches are found, the process returns false.
	 * Geared towards (not)includes.
	 * 
	 * 
	 * @param ctx
	 * @param goal
	 * @param checkMatch: should return true / false for a positive / negative
	 *                    example, respectively.
	 * @return
	 */

	protected boolean matchClauses(List<TriplePattern> clauses, MatchContext ctx, MatchGoals goal,
			Function<Void, Boolean> checkMatch, Finder continuation) {

//		System.out.println("univQuant.matchClauses");
//		String label = "[" + (goal == MatchGoals.NO_NEG ? "body" : "head") + "] ";
		if (clauses.isEmpty()) {
//			System.out.println(label + "checking match");
			boolean check = checkMatch.apply(null);
//			System.out.println(label + "check? " + check);
			return check;
		}

		clauses = ClauseSort.sortOnBoundedness(clauses, ctx.getEnv(), ctx.getGraph());
		TriplePattern clause = clauses.remove(0);
		TripleSet tripleSet = RuleUtil.createTripleSet(clause, ctx.getEnv(), ctx.getGraph(),
				continuation);
//		System.out.println("tripleSet: " + tripleSet.getClass() + " - " + tripleSet);
//		System.out.println("graph: " + ctx.getGraph().getClass());

//		System.out.println(label + "c(0)? " + clause);
		TripleForMatchIterator it = tripleSet.triples(ctx.getEnv());

		while (it.hasNext()) {
			Triple t = it.next();
//			System.out.println(label + "c? " + clause);
//			System.out.println(label + "t? " + t); // + " - " + ctx.getEnv());

			ctx.getEnv().push();
			boolean ret = false;

			boolean bound = RuleUtil.match(clause, t, !tripleSet.requiresMatching(), ctx.getEnv());
			if (bound) {
//				System.out.println(label + "bound: " + t);
				ret = matchClauses(clauses, ctx, goal, checkMatch, continuation);
			}
			ctx.getEnv().unwind();
			
			it.foundMatch(ret);

			// System.out.println(label + "ret? " + ret);
			if (bound) {
				switch (goal) {

				case NO_NEG:
					if (!ret) {
						it.close();
						return false;
					}
					break;

				case MIN_1_POS:
					if (ret) {
						it.close();
						return true;
					}
					break;

				default:
					break;
				}
			}
		}
		
		it.close();

		switch (goal) {

		case MIN_1_POS:
			return false;

		default:
			return true;
		}
	}

	protected class MatchContext {

		private List<TriplePattern> tripleSets;
		private BindingStack env;
		private Graph graph;

		public MatchContext(List<TriplePattern> clauses, RuleContext ruleCtx) {
			this(clauses, (BindingStack) ruleCtx.getEnv(), ruleCtx.getGraph());
		}

		public MatchContext(List<TriplePattern> clauses, BindingStack env, Graph graph) {
			this.env = env;
			this.graph = graph;

			this.tripleSets = clauses;
		}

		public List<TriplePattern> getTripleSets() {
			return tripleSets;
		}

		public BindingStack getEnv() {
			return env;
		}

		public Graph getGraph() {
			return graph;
		}
	}
}
