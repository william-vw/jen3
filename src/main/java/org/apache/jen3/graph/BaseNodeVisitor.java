package org.apache.jen3.graph;

import org.apache.jen3.graph.impl.LiteralLabel;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.graph.n3.Node_Quantifier;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;

public class BaseNodeVisitor implements NodeVisitor {

	@Override
	public Node visitAny(Node_ANY it, Object data) {
		return it;
	}

	@Override
	public Node visitBlank(Node_Blank it, BlankNodeId id, Object data) {
		return it;
	}

	@Override
	public Node visitLiteral(Node_Literal it, LiteralLabel lit, Object data) {
		return it;
	}

	@Override
	public Node visitURI(Node_URI it, String uri, Object data) {
		return it;
	}

	@Override
	public Node visitVariable(Node_Variable it, String name, Object data) {
		return it;
	}

	@Override
	public Node visitRuleVariable(Node_RuleVariable it, Object data) {
		return it;
	}

	@Override
	public Node visitQuickVariable(Node_QuickVariable it, Object data) {
		return it;
	}

	@Override
	public Node visitQuantifiedVariable(Node_QuantifiedVariable it, Object data) {
		return it;
	}

	@Override
	public Node visitQuantifier(Node_Quantifier it, Object data) {
		return it;
	}

	@Override
	public Node visitCollection(Node_Collection it, Object data) {
		return it;
	}

	@Override
	public Node visitCitedFormula(Node_CitedFormula it, Object data) {
		return it;
	}
}
