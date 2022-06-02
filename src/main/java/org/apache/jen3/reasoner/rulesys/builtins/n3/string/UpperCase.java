package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class UpperCase extends StringBuiltin {

	public UpperCase() {
		super(new BinaryFlowPattern((n,g) -> {
			return NodeFactory.createLiteralByValue(Util.parseString(n).toUpperCase(), XSDDatatype.XSDstring);

		}, null), true);
	}
}
