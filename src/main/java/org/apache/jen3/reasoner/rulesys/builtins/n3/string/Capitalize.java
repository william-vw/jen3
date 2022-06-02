package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.commons.lang3.StringUtils;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Capitalize extends StringBuiltin {

	public Capitalize() {
		super(new BinaryFlowPattern((n,g) -> {
			return NodeFactory.createLiteralByValue(StringUtils.capitalize(Util.parseString(n)), XSDDatatype.XSDstring);

		}, null), true);
	}
}