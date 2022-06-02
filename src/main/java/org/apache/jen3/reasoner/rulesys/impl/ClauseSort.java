package org.apache.jen3.reasoner.rulesys.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_ANY;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack.GroundCmd;
import org.apache.jen3.vocabulary.RDF;

/**
 * After doing some profiling work (using VisualVM), turns out that sorting
 * clauses took up a huge amount of time as it always recursively grounded all
 * terms; however, recursive grounding is only needed in certain cases when
 * sorting clauses. This class encapsulates that "grounding strategy".
 */

public class ClauseSort {

	public static NodeInputGrounding groundedInput = new NodeInputGrounding();

	public static List<TriplePattern> sortOnBoundedness(List<TriplePattern> clauses,
			BindingStack env, Graph graph) {

		return clauses.stream().sorted((c1, c2) -> {
			int score1 = scoreTripleBoundedness(c1, env, graph);
			int score2 = scoreTripleBoundedness(c2, env, graph);

			int ret = Integer.valueOf(score2).compareTo(score1);
			return ret;

		}).collect(Collectors.toList());
	}

	/**
	 * Callers need to access static 'groundedInput' field of this class (aim is to
	 * indicate this object is not re-created for each call but instead re-used).
	 */

	public static void groundForScoring(TriplePattern clause, BindingStack env, Graph graph) {
		Node s = clause.getSubject(), p = clause.getPredicate(), o = clause.getObject();

		// need to know whether predicate is a builtin
		Node sb, pb = env.groundShallow(p), ob;
		if (isBuiltin(pb, graph)) {
			groundForBuiltinScoring(clause, env, graph);
			groundedInput.pb = pb;

			// triple-patterns only require surface terms
		} else {
			sb = env.groundShallow(s);
			ob = env.groundShallow(o);

			groundedInput.setup(sb, pb, ob, false);
		}
	}

	/**
	 * Callers need to access static 'groundedInput' field of this class (aim is to
	 * indicate this object is not re-created for each call but instead re-used).
	 */

	public static void groundForBuiltinScoring(TriplePattern clause, BindingStack env,
			Graph graph) {

		Node s = clause.getSubject(), p = clause.getPredicate(), o = clause.getObject();

		// first, let's get surface-terms for s/o
		Node sb = env.groundShallow(s);
		Node ob = env.groundShallow(o);

		// currently, builtin input-checking (see builtinDelayScore) only requires
		// introspecting direct (level = 1) elements of collections
		// (this may change later on; and this code will break in that case)

		if (sb.isCollection())
			sb = env.groundConcrete(sb, new GroundCmd(1));
		if (ob.isCollection())
			ob = env.groundConcrete(ob, new GroundCmd(1));

		// (predicate is not being grounded; we already know it's a builtin, which means
		// it was grounded)

		groundedInput.setup(sb, p, ob, true);
	}

	public static int scoreTripleBoundedness(TriplePattern clause, BindingStack env, Graph graph) {
		Node s = clause.getSubject(), p = clause.getPredicate(), o = clause.getObject();

		groundForScoring(clause, env, graph);
		Node sb = groundedInput.sb, pb = groundedInput.pb, ob = groundedInput.ob;

		int score = 0;
		if (groundedInput.isBuiltin)
			score = builtinDelayScore(s, sb, p, pb, o, ob, graph);

		if (score >= 0)
			// @formatter:off
				score = scoreNodeBoundness(s, sb) * 3 
					+ scoreNodeBoundness(p, pb) * 2
					+ scoreNodeBoundness(o, ob) * 3;
			// @formatter:on

		// Log.info(RuleUtil.class, "score? " + sb + ", " + p + " , " + ob + " ? " + score);

		return score;
	}

	/**
	 * Score a Node in terms of groundedness - heuristic. Treats a variable as
	 * better than a wildcard because it constrains later clauses. Treats rdf:type
	 * as worse than any other ground node because that tends to link to lots of
	 * expensive rules.
	 */
	public static int scoreNodeBoundness(Node n, Node nb) {
		if (n instanceof Node_ANY) {
			return 0;
		} else if (n instanceof Node_RuleVariable) {
			if (n == nb) {
				return 1;
			} else if (nb.equals(RDF.type.asNode())) {
				return 2;
			} else {
				return 3;
			}
		} else {
			return 3;
		}
	}

	private static boolean isBuiltin(Node p, Graph graph) {
		BuiltinSet builtinSet = graph.getN3Config().getBuiltinSet();
		return builtinSet.isBuiltin(p);
	}

	public static int builtinDelayScore(Node s, Node sb, Node p, Node pb, Node o, Node ob,
			Graph graph) {
		BuiltinSet builtinSet = graph.getN3Config().getBuiltinSet();

		N3Builtin builtin = builtinSet.getBuiltin(pb);
		int score = builtin.delayScore(s, sb, o, ob, graph);

		return score;
	}

	// always re-use same object (this operation happens very often)
	public static class NodeInputGrounding {

		public Node sb;
		public Node pb;
		public Node ob;
		public boolean isBuiltin;

		public NodeInputGrounding setup(Node sb, Node pb, Node ob, boolean isBuiltin) {
			this.sb = sb;
			this.pb = pb;
			this.ob = ob;
			this.isBuiltin = isBuiltin;

			return this;
		}
	}
}
