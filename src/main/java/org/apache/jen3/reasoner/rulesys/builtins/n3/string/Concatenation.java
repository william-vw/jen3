package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.util.stream.Collectors;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Concatenation extends StringBuiltin {

	public Concatenation() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_Collection list = (Node_Collection) n;
			
			String str = list.getElements().stream().map(s -> Util.parseString(s)).collect(Collectors.joining(""));
			return NodeFactory.createLiteral(str, XSDDatatype.XSDstring);

		}, null), true);
	}
}