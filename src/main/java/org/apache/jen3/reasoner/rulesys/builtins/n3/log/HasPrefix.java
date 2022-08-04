package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class HasPrefix extends LogBuiltin {

	public HasPrefix() {
		super(new BinaryFlowPattern((n, g) -> {
			String namespace = Util.parseString(n);
			String prefix = g.getPrefixMapping().getNsURIPrefix(namespace);

			return NodeFactory.createLiteralByValue(prefix != null, XSDDatatype.XSDboolean);

		}, null), true);
	}
}