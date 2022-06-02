package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.util.stream.Collectors;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Join extends StringBuiltin {

	public Join() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection coll = (Node_Collection) n;

			Node_Collection elements = (Node_Collection) coll.getElement(0);
			String delim = coll.getElement(1).getLiteralLexicalForm();

			String delimStr = elements.getElements().stream().map(e -> e.getLiteralLexicalForm())
					.collect(Collectors.joining(delim));

			return NodeFactory.createLiteral(delimStr);

		}, null), true);
	}
}
