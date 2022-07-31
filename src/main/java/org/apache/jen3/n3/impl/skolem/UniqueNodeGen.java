package org.apache.jen3.n3.impl.skolem;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_Blank;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Skolem;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

public abstract class UniqueNodeGen {


	public abstract Node uniqueBlankNode(BindingStack env, Node node, Scope scope);

	public Node uniqueSkolem(Node_Blank bnode, N3ModelSpec spec) {
		return Skolem.gen(bnode);

//		String id = spec.getSkolemNs() + bnode.getBlankNodeLabel();
//		return NodeFactory.createURI(id);
	}
}
