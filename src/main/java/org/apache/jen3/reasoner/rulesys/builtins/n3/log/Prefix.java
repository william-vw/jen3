package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Node_Literal;
import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Prefix extends LogBuiltin {

	public Prefix() {
		super(new BinaryFlowPattern((n,g) -> {
			String namespace = ((Node_URI) n).getURI();
			
			String prefix = g.getPrefixMapping().getNsURIPrefix(namespace);
			if (prefix == null) {
				System.err.println("cannot find prefix for namespace: " + namespace);
				return null;
			}
			
			return NodeFactory.createLiteral(prefix);
			
		}, (n,g) -> {
			String prefix = ((Node_Literal) n).getLiteralLexicalForm();
			
			String namespace = g.getPrefixMapping().getNsPrefixURI(prefix);
			if (namespace == null) {
				System.err.println("cannot find namespace for prefix: " + prefix);
				return null;
			}
			
			return NodeFactory.createURI(namespace);
			
		}), true);
	}
}
