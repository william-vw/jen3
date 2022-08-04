package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class LocalName extends LogBuiltin {

	private static Pattern p = Pattern.compile(".+(#|/)(.*)");

	public LocalName() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_URI uri = (Node_URI) n;

			// doesn't work for e.g.,
			// https://bioportal.bioontology.org/ontologies/DTO.owl#DTO:0001939

//			String str = uri.getLocalName();

			String uriStr = uri.getURI();
			Matcher m = p.matcher(uriStr);
			if (m.matches()) {
				String ln = m.group(2);
				return NodeFactory.createLiteral(ln);

			} else {
				System.err.println("confused about URI " + uriStr);
				return null;
			}

		}, null), true);
	}
}
