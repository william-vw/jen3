package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Substring extends StringBuiltin {

	public Substring() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection list = (Node_Collection) n;
			String str = Util.parseString(list.getElement(0));
			
			int from = Util.parseInt(list.getElement(1));
			int to = str.length();
			
			Node node2 = list.getElement(2);
			if (node2.isConcrete())
				to = Util.parseInt(list.getElement(2));

			if (from < 0 || to > str.length() || from > to)
				return null;
			else {
				String substr = str.substring(from, to);
				return NodeFactory.createLiteralByValue(substr, XSDDatatype.XSDstring);
			}

		}, null), true);
	}
}
