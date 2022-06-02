package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class AnyIC extends InputConstraint {

	private static final long serialVersionUID = -7277946894110997836L;

	public AnyIC() {
		super(DefaultICs.ANY);
	}

	@Override
	protected boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return true;
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		return new ICConvert(n);
	}
}
