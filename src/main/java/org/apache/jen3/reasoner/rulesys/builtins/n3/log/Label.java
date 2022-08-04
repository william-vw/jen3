package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.n3.impl.skolem.UniqueIdViaKey;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Label extends LogBuiltin {

	public Label() {
		super(new BinaryFlowPattern((n, g) -> {
//			Node_Blank b = (Node_Blank) n;
//			String label = b.getBlankNodeLabel();			
			String label = UniqueIdViaKey.getSeqBnodeId(n);

			return NodeFactory.createLiteral(label);

		}, null), true);
	}
}
