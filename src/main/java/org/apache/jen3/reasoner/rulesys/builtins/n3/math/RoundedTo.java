package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class RoundedTo extends MathBuiltin {

	public RoundedTo() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_Collection c = (Node_Collection) n;
			Node nr = c.getElement(0);
			Node nrDigits = c.getElement(1);

			Number res = Util.rounded(nr, nrDigits);
			return NodeFactory.createLiteralByValue(res, XSDDatatype.getFromCls(res.getClass()));

		}, null), true);
	}
}
