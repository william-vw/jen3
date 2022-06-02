package org.apache.jen3.reasoner.rulesys.builtins.n3.file;

import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class ListFiles extends FileBuiltin {

	public ListFiles() {
		super(new BinaryFlowPattern((n, g) -> {

			if (g.getN3Config().getBuiltinConfig().isPrintDownloads())
				System.out.println("[file:listFiles] getting: " + n.getURI());

			Node_Collection files = Util.retrieveFiles(n.getURI());
			return files;

		}, null), true);
	}
}
