package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class ParsedAsN3 extends LogBuiltin {

	public ParsedAsN3() {
		super(new BinaryFlowPattern((n, g) -> {
			String content = Util.parseString(n);
			N3Model out = Util.parseN3(content, g.getPrefixMapping().getBase());

			return NodeFactory.createCitedFormula(g.getScope().sub(Scopes.GRAPH), out);

		}, (n, g) -> {
			String str = Util.parseString(n);
			return NodeFactory.createLiteral(str);

		}), true);
	}
}
