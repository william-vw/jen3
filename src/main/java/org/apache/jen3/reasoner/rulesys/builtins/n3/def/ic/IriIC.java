package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class IriIC extends InputConstraint {

	private static final long serialVersionUID = -5165449022806526337L;

	public IriIC() {
		super(DefaultICs.IRI);
	}

	@Override
	public boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		return n.isURI();
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace))
			return new ICConvert(n);
		return noMatch;
	}
}