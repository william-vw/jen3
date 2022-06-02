package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.UnaryFlowPattern;

public class Uuid extends LogBuiltin {

	public Uuid() {
		super(new UuidFlowPattern(), true);
	}

	private static class UuidFlowPattern extends UnaryFlowPattern {

		// per instantiation of this builtin, keep unique ID
		// TODO this should be per reasoning round (?)
		private UUID uuid = UUID.randomUUID();

		@Override
		public Node flow(Node n, Graph g) {
			// (UUID based on current instance, subject & object of statement)
			String str = this.uuid.toString() + "S" + Util.parseString(s) + "O" + Util.parseString(o);
			String uuid = DigestUtils.sha1Hex(str.getBytes());

			return NodeFactory.createLiteral(uuid);
		}
	}
}
