package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Bound extends LogBuiltin {

	public Bound() {
		super(new BinaryFlowPattern((n, g) -> {
			return NodeFactory.createLiteralByValue(n.isConcrete(), XSDDatatype.XSDboolean);

		}, null), true);
	}
}
