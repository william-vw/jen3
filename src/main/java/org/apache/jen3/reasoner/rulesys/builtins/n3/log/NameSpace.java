package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class NameSpace extends LogBuiltin {

	public NameSpace() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_URI uri = (Node_URI) n;
			String str = uri.getNameSpace();
			
			return NodeFactory.createLiteral(str);
			
		}, null), true);
	}
}
