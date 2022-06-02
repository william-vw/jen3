package org.apache.jen3.reasoner.rulesys.builtins.n3.flow;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;

public abstract class FlowPattern {

	protected N3Builtin builtin;

	public N3Builtin getBuiltin() {
		return builtin;
	}

	public void setBuiltin(N3Builtin builtin) {
		this.builtin = builtin;
	}

	public abstract void setup(Node s, Node o, BindingEnvironment env);

	// (passing env here avoids having to keep it as state)
	public abstract boolean apply(BindingEnvironment env, Graph graph);

	public abstract Triple instantiate(Node predicate);
}
