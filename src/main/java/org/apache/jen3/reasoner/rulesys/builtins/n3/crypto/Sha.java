package org.apache.jen3.reasoner.rulesys.builtins.n3.crypto;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Sha extends CryptoBuiltin {

	public Sha() {
		super(new BinaryFlowPattern((n,g) -> {
			String s = Util.parseString(n);
			String s2 = DigestUtils.sha1Hex(s.getBytes());
			
			return NodeFactory.createLiteral(s2);

		}, null), true);
	}
}
