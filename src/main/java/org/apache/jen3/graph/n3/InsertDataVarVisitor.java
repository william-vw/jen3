package org.apache.jen3.graph.n3;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;

public class InsertDataVarVisitor extends InsertVarVisitor {

	@Override
	public Node visitRuleVariable(Node_RuleVariable it, Object data) {
		return it.getOriginal();
	}
}