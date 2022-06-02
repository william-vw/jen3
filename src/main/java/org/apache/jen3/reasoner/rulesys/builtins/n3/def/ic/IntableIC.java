package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class IntableIC extends InputConstraint {

	private static final long serialVersionUID = 6106616736077610995L;

	public IntableIC() {
		super(DefaultICs.INTABLE);
	}

	@Override
	public boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return Util.isInt(n);
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace)) {
			Integer nr = Util.parseInt(n);
			RDFDatatype type = XSDDatatype.XSDint;

			return new ICConvert(NodeFactory.createLiteralByValue(nr, type));
		}
		return noMatch;
	}
}
