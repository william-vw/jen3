package org.apache.jen3.reasoner.rulesys.builtins.n3.flow;

import java.util.function.BiFunction;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;

public class UnaryFlowPattern extends FlowPattern {
	
	protected BiFunction<Node, Graph, Node> flow;
	
	protected Node s;
	protected Node o;
	
	protected Node result;

	public UnaryFlowPattern() {
	}
	
	public UnaryFlowPattern(BiFunction<Node, Graph, Node> flow) {
		this.flow  = flow;
	}

	@Override
	public void setup(Node s, Node o, BindingEnvironment env) {		
		this.s = (s.isVariable() ? env.ground(s) : s);
		this.o = (o.isVariable() ? env.ground(o) : o);
	}
	
	@Override
	public boolean apply(BindingEnvironment env, Graph graph) {
		Node result = flow(s, graph);
		if (o.isConcrete())
			return compare(o, result, env);
		
		else {
			this.result = result;
			return result != null;
		}
	}
	
	protected Node flow(Node n, Graph graph) {
		return flow.apply(n, graph);
	}
	
	private boolean compare(Node original, Node result, BindingEnvironment env) {
		if (result == null)
			return false;
		
		return RuleUtil.singleMatch(original, result, RuleUtil.configDefault, (BindingStack) env);
	}

	@Override
	public Triple instantiate(Node predicate) {
		return new Triple(s, predicate, result);
	}
}