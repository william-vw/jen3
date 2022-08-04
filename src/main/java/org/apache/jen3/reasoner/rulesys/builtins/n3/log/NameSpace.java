package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;
import org.apache.jena.atlas.logging.Log;

public class NameSpace extends LogBuiltin {

	private static Pattern p = Pattern.compile("(.+)(#|/).*");

	public NameSpace() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_URI uri = (Node_URI) n;

			// doesn't work for e.g.,
			// https://bioportal.bioontology.org/ontologies/DTO.owl#DTO:0001939

//			String str = uri.getNamespace();

			String uriStr = uri.getURI();
			Matcher m = p.matcher(uriStr);
			if (m.matches()) {
				String ns = m.group(1);
				String sep = m.group(2);
				return NodeFactory.createLiteral(ns + sep);

			} else {
				Log.error(LocalName.class, "cannot find namespace of URI: " + uriStr);
				return null;
			}

		}, null), true);
	}
}
