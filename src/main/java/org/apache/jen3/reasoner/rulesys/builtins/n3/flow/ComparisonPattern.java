package org.apache.jen3.reasoner.rulesys.builtins.n3.flow;

import java.util.function.BiFunction;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;

public class ComparisonPattern extends BinaryFlowPattern {

	public ComparisonPattern(BiFunction<Node, Node, Boolean> cmp) {
		BiFunction<Node, Node, Boolean> invCmp = (n1, n2) -> cmp.apply(n2, n1);

		build(new DefaultFlowVertex(new CmpEdge(cmp)), new DefaultFlowVertex(new CmpEdge(invCmp)));
	}

	private class CmpEdge extends DefaultFlowEdge {

		private BiFunction<Node, Node, Boolean> cmp;

		public CmpEdge(BiFunction<Node, Node, Boolean> cmp) {
			this.cmp = cmp;
		}

		@Override
		public boolean compare(Node node, BindingEnvironment env) {
			return cmp.apply(this.result, node);
		}
	}
}
