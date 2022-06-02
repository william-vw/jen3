package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class StringableIC extends InputConstraint {

	private static final long serialVersionUID = -4506340061628150982L;

	public StringableIC() {
		super(DefaultICs.STRINGABLE);
	}

	protected StringableIC(DefaultICs type) {
		super(type);
	}

	@Override
	protected boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return Util.isStringable(n);
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace))
			return new ICConvert(NodeFactory.createLiteral(Util.parseString(n)));
		
		return noMatch;
	}
}
