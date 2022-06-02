package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import java.util.function.Function;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class MathOnNumeralsPattern extends BinaryFlowPattern {

	public MathOnNumeralsPattern(Function<Node, Number> op) {
		build(new DefaultFlowVertex(new MathOpEdge(op)), new DefaultFlowVertex());
	}

	public MathOnNumeralsPattern(Function<Node, Number> op, Function<Node, Number> inverseOp) {
		build(new DefaultFlowVertex(new MathOpEdge(op)), new DefaultFlowVertex(new MathOpEdge(inverseOp)));
	}

	private class MathOpEdge extends DefaultFlowEdge {

		private Function<Node, Number> op;

		public MathOpEdge(Function<Node, Number> op) {
			this.op = op;
		}

		@Override
		public Node flow(Node node, Graph graph) {
			Number ret = op.apply(node);
			return Util.createNumeral(ret);
		}
	}
}
