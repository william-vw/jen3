package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import java.util.function.Function;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class MathOnNumbersPattern extends BinaryFlowPattern {

	public MathOnNumbersPattern(Function<Number, Number> op) {
		build(new DefaultFlowVertex(new MathOpEdge(op)), new DefaultFlowVertex());
	}

	public MathOnNumbersPattern(Function<Number, Number> op, Function<Number, Number> inverseOp) {
		build(new DefaultFlowVertex(new MathOpEdge(op)), new DefaultFlowVertex(new MathOpEdge(inverseOp)));
	}

	private class MathOpEdge extends DefaultFlowEdge {

		private Function<Number, Number> op;

		public MathOpEdge(Function<Number, Number> op) {
			this.op = op;
		}

		@Override
		public Node flow(Node node, Graph graph) {
			Number ret = op.apply(Util.parseNumber(node));
			return Util.createNumeral(ret);
		}
	}
}
