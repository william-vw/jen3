package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Length extends ListBuiltin {

	public Length() {
		super(new BinaryFlowPattern((n,g) -> {
			int len = ((Node_Collection) n).getElements().size();
			return NodeFactory.createLiteralByValue(len, XSDDatatype.XSDint);
			
		}, null), true);
	}
}
