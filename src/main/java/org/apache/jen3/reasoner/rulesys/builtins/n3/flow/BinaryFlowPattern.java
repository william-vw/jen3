package org.apache.jen3.reasoner.rulesys.builtins.n3.flow;

import java.util.function.BiFunction;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;

public class BinaryFlowPattern extends FlowPattern {

	protected BinaryFlowVertex vertex1;
	protected BinaryFlowVertex vertex2;

	public BinaryFlowPattern() {
	}

	public BinaryFlowPattern(BiFunction<Node, Graph, Node> flow1, BiFunction<Node, Graph, Node> flow2) {
		build(flow1, flow2);
	}

	public void build(BiFunction<Node, Graph, Node> flow1, BiFunction<Node, Graph, Node> flow2) {
		build(new DefaultFlowVertex(new DefaultFlowEdge(flow1)), new DefaultFlowVertex(new DefaultFlowEdge(flow2)));
	}

	public BinaryFlowPattern build(BinaryFlowVertex vertex1, BinaryFlowVertex vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		vertex1.getFlowEdge().connect(vertex2.getFlowEdge());

		return this;
	}

	@Override
	public void setup(Node s, Node o, BindingEnvironment env) {
		vertex1.setup(s, env);
		vertex2.setup(o, env);
	}

	@Override
	public boolean apply(BindingEnvironment env, Graph graph) {
		return vertex1.apply(env, graph);
	}

	@Override
	public Triple instantiate(Node predicate) {
		Node subject = vertex1.getResult();
		Node object = vertex2.getResult();

		return new Triple(subject, predicate, object);
	}

	public abstract class BinaryFlowVertex {

		private Node node;
		private BinaryFlowEdge edge;

		public BinaryFlowVertex() {
		}

		public BinaryFlowVertex(BinaryFlowEdge edge) {
			this.edge = edge;
			edge.setVertex(this);
		}

		public BinaryFlowEdge getFlowEdge() {
			return edge;
		}

		public boolean isConcrete() {
			return node.isConcrete();
		}

		public void setup(Node node, BindingEnvironment env) {
			this.node = (node.isVariable() ? env.ground(node) : node);
			edge.reset();
		}

		public Node getResult() {
			if (edge != null)
				return edge.getResult();
			else
				return node;
		}

		public boolean apply(BindingEnvironment env, Graph graph) {
			if (edge != null)
				return edge.fromVertex(node, env, graph);
			else {
				return true;
			}
		}
	}

	public class DefaultFlowVertex extends BinaryFlowVertex {

		public DefaultFlowVertex() {
			super(new DefaultFlowEdge());
		}

		public DefaultFlowVertex(BinaryFlowEdge edge) {
			super(edge);
		}
	}

	public abstract class BinaryFlowEdge {

		protected BinaryFlowEdge otherEdge;
		protected BinaryFlowVertex vertex;
		protected Node result;
		protected Node flowResult;

		public void setVertex(BinaryFlowVertex vertex) {
			this.vertex = vertex;
		}

		public void reset() {
			result = null;
			flowResult = null;
		}

		public void connect(BinaryFlowEdge otherEdge) {
			setOtherEdge(otherEdge);

			otherEdge.setOtherEdge(this);
		}

		private void setOtherEdge(BinaryFlowEdge otherEdge) {
			this.otherEdge = otherEdge;
		}

		public boolean fromVertex(Node node, BindingEnvironment env, Graph graph) {
//			Log.info(getClass(), "fromVertex: concrete=" + node.isConcrete() + " ; otherEdge? done=" + otherEdge.isDone()
//					+ " ; concrete=" + otherEdge.isConcrete());

			this.result = node;
			if (node.isConcrete()) {

				if (otherEdge.isDone()) {
					if (otherEdge.isConcrete())
						return otherEdge.compare(node, env);
					else {
						this.flowResult = flow(node, graph);
						return otherEdge.finish(flowResult, env);
					}

				} else {
					this.flowResult = flow(node, graph);
					return otherEdge.fromEdge(env, graph);
				}

			} else {
				if (otherEdge.isDone()) {
					if (otherEdge.isConcrete())
						return finish(otherEdge.getFlowResult(), env);
					else
						return finish(graph);
				} else
					return otherEdge.fromEdge(env, graph);
			}
		}

		public boolean isDone() {
			return result != null;
		}

		public boolean isConcrete() {
			return vertex.isConcrete();
		}

		public Node getResult() {
			return result;
		}

		public Node getFlowResult() {
			return flowResult;
		}

		protected boolean fromEdge(BindingEnvironment env, Graph graph) {
			return vertex.apply(env, graph);
		}

		protected abstract Node flow(Node node, Graph graph);

		protected abstract boolean compare(Node result, BindingEnvironment env);

		protected boolean finish(Node result, BindingEnvironment env) {
			this.result = result;
			return (result != null);
		}

		protected boolean finish(Graph g) {
			return true;
		}
	}

	public class DefaultFlowEdge extends BinaryFlowEdge {

		private BiFunction<Node, Graph, Node> flow;

		public DefaultFlowEdge() {
		}

		public DefaultFlowEdge(BiFunction<Node, Graph, Node> flow) {
			this.flow = flow;
		}

		@Override
		protected Node flow(Node node, Graph graph) {
			if (flow != null)
				return flow.apply(node, graph);
			else
				return node;
		}

		@Override
		protected boolean compare(Node result, BindingEnvironment env) {
			if (result == null || flowResult == null)
				return false;

			return RuleUtil.singleMatch(result, flowResult, RuleUtil.configDefault, (BindingStack) env);
		}
	}
}