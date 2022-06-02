package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.util.regex.Pattern;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Replace extends StringBuiltin {

	public Replace() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection c = (Node_Collection) n;

			String str = Util.parseString(c.getElement(0));
			Pattern regex = (Pattern) c.getElement(1).getLiteralValue();
			String repl = Util.parseString(c.getElement(2));
			repl = repl.replace("\\\\", "\\");
			
			String result = regex.matcher(str).replaceAll(repl);
			return NodeFactory.createLiteralByValue(result, XSDDatatype.XSDstring);

		}, null), true);
	}
}