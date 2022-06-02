package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class LiteralIC extends InputConstraint {

	private static final long serialVersionUID = 8758177017769833624L;

	public LiteralIC() {
		super(DefaultICs.LITERAL);
	}

	public LiteralIC(DefaultICs type) {
		super(type);
	}

	@Override
	public boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return n.isLiteral();
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace))
			return new ICConvert(n);
		return noMatch;
	}
}
