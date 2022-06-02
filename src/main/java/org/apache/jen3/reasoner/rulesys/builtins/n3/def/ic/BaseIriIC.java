package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import static org.apache.jen3.n3.N3MistakeTypes.*;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class BaseIriIC extends InputConstraint {

	private static final long serialVersionUID = -8916453507659545416L;

	public BaseIriIC() {
		super(DefaultICs.BASE_IRI);
	}

	@Override
	public boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		if (!n.isURI())
			return false;

		String base = graph.getPrefixMapping().getBase();
		if (base == null) {
			if (graph.getN3Config().hasFeedbackFor(BUILTIN_NO_BASE))
				graph.getN3Config().getFeedback(BUILTIN_NO_BASE).doDefaultAction();

			return false;
		}

		String baseUri = TermUtil.dropFragment(base);
		String uri = TermUtil.dropFragment(n.getURI());

		return (baseUri != null && uri.equals(baseUri));
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (doCheck(n, id, graph, trace))
			return new ICConvert(n);
		return noMatch;
	}
}