package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Rounded extends MathBuiltin {

	public Rounded() {
		super(new BinaryFlowPattern((n,g) -> {
			Number res = Util.rounded(n);
			return NodeFactory.createLiteralByValue(res, XSDDatatype.getFromCls(res.getClass()));

		}, null), true);
	}
}
