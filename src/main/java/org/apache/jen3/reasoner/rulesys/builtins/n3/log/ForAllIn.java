package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.reasoner.rulesys.builtins.n3.log.UniversalQuantification.MatchGoals.MIN_1_POS;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.log.UniversalQuantification.MatchGoals.NO_NEG;

import java.util.List;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;

public class ForAllIn extends UniversalQuantification {

	@Override
	protected boolean match(Node sb, Node ob, BindingStack env, Graph graph, Finder continuation) {
		Node_Collection list = (Node_Collection) sb;

		Graph target = getTargetGraph(ob, graph);

		Node_CitedFormula body = (Node_CitedFormula) list.getElements().get(0);
		Node_CitedFormula head = (Node_CitedFormula) list.getElements().get(1);

		List<TriplePattern> bodyClauses = RuleUtil
				.toTriplePatterns(body.getContents().getGraph().find().toList());
		List<TriplePattern> headClauses = RuleUtil
				.toTriplePatterns(head.getContents().getGraph().find().toList());

		return match(bodyClauses, headClauses, env, target, continuation);
	}

	private boolean match(List<TriplePattern> body, List<TriplePattern> head, BindingStack env,
			Graph graph, Finder continuation) {

		MatchContext bCtx = new MatchContext(body, env, graph);
		MatchContext hCtx = new MatchContext(head, env, graph);

		return matchClauses(bCtx.getTripleSets(), bCtx, NO_NEG,
				(v) -> matchClauses(hCtx.getTripleSets(), hCtx, MIN_1_POS, (v2) -> true,
						continuation),
				continuation);
	}
}