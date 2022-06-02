package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;

public class SemanticsOrError extends LogBuiltin {

	public SemanticsOrError() {
		super(new SemanticsFlowPattern((e) -> NodeFactory.createLiteral(e)), true);
	}
}