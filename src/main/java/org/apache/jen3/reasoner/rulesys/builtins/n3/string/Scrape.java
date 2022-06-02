package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Scrape extends StringBuiltin {

	public Scrape() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_Collection c = ((Node_Collection) n);
			String str = Util.parseString(c.getElement(0));
			Pattern regex = (Pattern) c.getElement(1).getLiteralValue();

			Matcher m = regex.matcher(str);
			if (m.matches()) {
				if (m.groupCount() != 1)
					return null;

				String scraped = m.group(1);
				return NodeFactory.createLiteralByValue(scraped, XSDDatatype.XSDstring);
				
			} else
				return null;

		}, null), true);
	}
}
