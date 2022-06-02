package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.List;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;

public abstract class LogInclude extends LogCompare {

	protected LogInclude(boolean inverse) {
		super(inverse);
	}

	@Override
	public boolean isUniversal(Node sb, Node ob) {
		// if either of these refer to the base IRI, then their contents will be the
		// entire current document (= universal builtin)

		return sb.isURI() || ob.isURI();
	}

	@Override
	protected boolean match(Node sb, Node ob, BindingStack env, Graph graph) {
		List<Triple> subClauses = getClauses(ob, graph);

		// base IRI = current graph: use "optimized" version
		if (sb.isURI())
			return RuleUtil.includes(subClauses, 0, graph, env);

		else {
			List<Triple> supClauses = getClauses(sb, graph);
			return RuleUtil.includes(subClauses, 0, supClauses, env);
		}
	}

	protected List<Triple> getClauses(Node nb, Graph graph) {
		// base IRI - get all triples in graph
		if (nb.isURI())
			return graph.find().toList();

		else {
			Node_CitedFormula cf = (Node_CitedFormula) nb;
			return getTriples(cf);
		}
	}

	protected List<Triple> getTriples(Node_CitedFormula cf) {
		return cf.getContents().getGraph().find().toList();
	}
}
