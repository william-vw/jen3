package org.apache.jen3.graph.n3;

import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.TriplePattern;

/**
 * Replaces quick-vars, quantified-vars with variables (depending on the
 * subclass; e.g., rule-variables or Node_ANYs).
 * 
 * 
 * @author wvw
 *
 */

public abstract class InsertVarVisitor extends ModifyFormulaNodeVisitor {

//	public static int nrNewGraphs = 0;
//	public static int nrNewColls = 0;

	protected TriplePattern visit(Triple t, Object data) {
		return new TriplePattern(t.getSubject().visitWith(this, data), t.getPredicate().visitWith(this, data),
				t.getObject().visitWith(this, data));
	}
	
	@Override
	public Node visitCitedFormula(Node_CitedFormula it, Object data) {
		if (it.includesVars()) {
//			nrNewGraphs++;
			return super.visitCitedFormula(it, data);

		} else
			return it;
	}

	// see superclass for visitCitedFormula method

	@Override
	protected Triple visitFormulaContent(Triple t, Object data) {
		Node s = t.getSubject().visitWith(this, data);
		Node p = t.getPredicate().visitWith(this, data);
		Node o = t.getObject().visitWith(this, data);

		if (s != t.getSubject() || p != t.getPredicate() || o != t.getObject())
			return new Triple(s, p, o);
		else
			return t;
	}

	@Override
	public Node visitCollection(Node_Collection it, Object data) {
		if (it.includesVars()) {
//			nrNewColls++;
			return new Node_Collection(
					it.getElements().stream().map(n -> n.visitWith(this, data)).collect(Collectors.toList()));
		} else
			return it;
	}
}
