package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICCheck;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICStatementConvert;

public class BoolStatementIC extends StatementInputConstraint {

	private static final long serialVersionUID = -1707412063369060069L;

	private boolean value;
	
	public BoolStatementIC(InputConstraintsDefinition inputCnstr, boolean value) {
		super(inputCnstr, StatementICTypes.BOOL);
		
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public ICCheck check(Node s, Node o, Graph graph) {
		return new ICCheck(value);
	}

	@Override
	public ICStatementConvert convert(Node s, Node o, Graph graph) {
		return new ICStatementConvert(value, new ICConvert(s), new ICConvert(o), this);
	}

	public String toString() {
		return "{ " + value + " }";
	}
}
