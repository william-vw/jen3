package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Format extends StringBuiltin {

	public Format() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection coll = (Node_Collection) n;

			String template = coll.getElement(0).getLiteralLexicalForm();
			Object[] args = coll.getElements().stream().skip(1).map(e -> e.getLiteralLexicalForm()).toArray();

			try {				
				String str = String.format(template, args);
				return NodeFactory.createLiteral(str);
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}, null), true);
	}
}
