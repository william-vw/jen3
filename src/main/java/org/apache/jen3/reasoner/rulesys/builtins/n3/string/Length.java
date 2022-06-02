package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Length extends StringBuiltin {

	public Length() {
		super(new BinaryFlowPattern(
				(n,g) -> NodeFactory.createLiteralByValue(Util.parseString(n).length(), XSDDatatype.XSDinteger), null),
				true);
	}
}
