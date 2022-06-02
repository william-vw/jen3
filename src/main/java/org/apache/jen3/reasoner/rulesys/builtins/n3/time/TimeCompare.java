package org.apache.jen3.reasoner.rulesys.builtins.n3.time;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.datatypes.xsd.XSDDateTime;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public abstract class TimeCompare extends TimeBuiltin {

	public TimeCompare() {
		flowPattern = new BinaryFlowPattern((n,g) -> {
			XSDDateTime t = (XSDDateTime) n.getLiteralValue();
			Pair<Object, XSDDatatype> p = getCmp(t);

			return NodeFactory.createLiteralByValue(p.getLeft(), p.getRight());

		}, null);

		flowPatternSingleton = true;
	}

	public abstract Pair<Object, XSDDatatype> getCmp(XSDDateTime t);
}