package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class NumberableIC extends InputConstraint {

	private static final long serialVersionUID = -7361443416524477938L;

	public NumberableIC() {
		super(DefaultICs.NUMBERABLE);
	}

	public NumberableIC(DefaultICs type) {
		super(type);
	}

	@Override
	public boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return Util.isNumeric(n);
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace)) {
			Number nr = Util.parseNumber(n);
			RDFDatatype type = XSDDatatype.getFromCls(nr.getClass());

			return new ICConvert(NodeFactory.createLiteralByValue(nr, type));
		}
		return noMatch;
	}
}
