package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Uri extends LogBuiltin {

	public Uri() {
		super(new BinaryFlowPattern((n,g) -> {
			String str = ((Node_URI) n).getURI();
			return NodeFactory.createLiteral(str);
			
		}, (n,g) -> {
			String str = (String) n.getLiteralValue();
			return NodeFactory.createURI(str);
			
		}), true);
	}
}