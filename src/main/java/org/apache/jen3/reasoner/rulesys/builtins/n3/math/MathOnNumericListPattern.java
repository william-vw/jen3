package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class MathOnNumericListPattern extends BinaryFlowPattern {

	private BinaryFlowPattern defaultPattern;
	private NestedVarPattern nestedVarPattern;

	private BinaryFlowPattern curPattern;

	public MathOnNumericListPattern(MathOperations op) {
		defaultPattern = new BinaryFlowPattern((n,g) -> {
			Node_Collection list = (Node_Collection) n;
			Number result = Util.applyOp(list.getElements(), op);
			
			return Util.createNumeral(result);

		}, null);

		nestedVarPattern = new NestedVarPattern(op);
	}

	@Override
	public void setup(Node s, Node o, BindingEnvironment env) {
		Node_Collection list = (Node_Collection) s;
		if (list.getElements().stream().anyMatch((n) -> n.isVariable()))
			curPattern = nestedVarPattern;
		else
			curPattern = defaultPattern;

		curPattern.setup(s, o, env);
	}

	@Override
	public boolean apply(BindingEnvironment env, Graph graph) {
		return curPattern.apply(env, graph);
	}

	@Override
	public Triple instantiate(Node predicate) {
		return curPattern.instantiate(predicate);
	}

	private class NestedVarPattern extends BinaryFlowPattern {

		private Node_Collection list;
		private int varIdx;

		public NestedVarPattern(MathOperations op) {
			build(new DefaultFlowVertex(), new InvMathVertex(op));
		}

		@Override
		public void setup(Node s, Node o, BindingEnvironment env) {
			this.list = (Node_Collection) s;
			Node var = getVar();

			vertex1.setup(var, env);
			((InvMathVertex) vertex2).setup(o, list, env);
		}

		private Node getVar() {
			for (int i = 0; i < list.size(); i++) {
				Node n = list.getElement(i);
				if (n.isVariable()) {
					varIdx = i;
					return n;
				}
			}
			return null;
		}

		@Override
		public Triple instantiate(Node predicate) {
			Node value = vertex1.getResult();
			Node object = vertex2.getResult();

			Node_Collection subject = NodeFactory.createCollection(list.getElements());
			subject.setElement(varIdx, value);

			return new Triple(subject, predicate, object);
		}
	}

	private class InvMathVertex extends DefaultFlowVertex {

		private Node_Collection list;

		public InvMathVertex(MathOperations op) {
			super(new InvMathEdge(op));
		}

		public void setup(Node node, Node_Collection list, BindingEnvironment env) {
			setup(node, env);
			this.list = list;
		}

		public Node_Collection getList() {
			return list;
		}
	}

	private class InvMathEdge extends DefaultFlowEdge {

		private MathOperations op;

		public InvMathEdge(MathOperations op) {
			this.op = op;
		}

		@Override
		protected Node flow(Node node, Graph graph) {
			Node rhs = node;

			Node_Collection list = ((InvMathVertex) vertex).getList();
			List<Node> numbers = list.getElements().stream().filter((n) -> n.isConcrete()).collect(Collectors.toList());
			Node lhs = Util.createNumeral(Util.applyOp(numbers, op));

			Number cmp = Util.applyOp(op.inverse(), rhs, lhs);
			return Util.createNumeral(cmp);
		}
	}
}
