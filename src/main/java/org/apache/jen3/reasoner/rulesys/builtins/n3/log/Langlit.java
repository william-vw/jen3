package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;

public class Langlit extends ToLit {

	@Override
	protected Node litFromExtra(Node string, Node extra) {
		return NodeFactory.createLiteral(string.getLiteralValue().toString(), extra.getLiteralValue().toString());
	}

	@Override
	protected Node extraFromLit(Node lit) {
		String lang = lit.getLiteralLanguage().substring(1);
		return NodeFactory.createLiteral(lang);
	}
}
