package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.io.IOException;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.n3.impl.N3ModelImpl.N3GraphConfig;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Content extends LogBuiltin {

	public Content() {
		super(new BinaryFlowPattern((n, g) -> {
			if (g.getN3Config().getBuiltinConfig().isPrintDownloads())
				System.out.println("[log:content] getting: " + n.getURI());

			try {
				String content = Util.retrieveString(n.getURI(), (N3GraphConfig) g.getConfig());

				Node ret = NodeFactory.createLiteral(content);
				return ret;

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;

		}, null), true);
	}
}
