package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.util.regex.Pattern;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class ReplaceAll extends StringBuiltin {

	public ReplaceAll() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection c = (Node_Collection) n;

			String str = Util.parseString(c.getElement(0));
			Node_Collection regexs = (Node_Collection) c.getElement(1);
			Node_Collection repls = (Node_Collection) c.getElement(2);

			if (regexs.size() != repls.size()) {
				return null;
			}
			
			String result = str;
			for (int i = 0; i < regexs.size(); i++) {
				Pattern regex = (Pattern) regexs.getElement(i).getLiteralValue();
				String repl = Util.parseString(repls.getElement(i));

				result = regex.matcher(result).replaceAll(repl);
			}

			return NodeFactory.createLiteralByValue(result, XSDDatatype.XSDstring);

		}, null), true);
	}
}