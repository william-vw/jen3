package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;
import org.apache.jena.atlas.logging.Log;

public class Prefix extends LogBuiltin {

	public Prefix() {
		super(new BinaryFlowPattern((n, g) -> {
			String namespace = Util.parseString(n);

			String prefix = g.getPrefixMapping().getNsURIPrefix(namespace);
			if (prefix == null) {
				Log.error(Prefix.class, "cannot find prefix for namespace: " + namespace);
				return null;
			}

			return NodeFactory.createLiteral(prefix);

		}, (n, g) -> {
			String prefix = Util.parseString(n);

			String namespace = g.getPrefixMapping().getNsPrefixURI(prefix);
			if (namespace == null) {
				Log.error(Prefix.class, "cannot find namespace for prefix: " + prefix);
				return null;
			}

			return NodeFactory.createLiteral(namespace);

		}), true);
	}
}
