package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class VariableIC extends InputConstraint {

	private static final long serialVersionUID = -3812357877900679225L;

	public VariableIC() {
		super(DefaultICs.VARIABLE);
	}

	@Override
	protected boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return n.isVariable();
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace))
			return new ICConvert(n);
		return noMatch;
	}
}
