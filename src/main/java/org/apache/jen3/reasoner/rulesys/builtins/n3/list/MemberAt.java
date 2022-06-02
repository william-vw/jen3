package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class MemberAt extends ListBuiltin {

	public MemberAt() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection c = (Node_Collection) n;
			Node_Collection list = (Node_Collection) c.getElement(0);
			int at = Util.parseInt(c.getElement(1));

			if (at < 0 || at >= list.size())
				return null;

			return list.getElement(at);

		}, null), true);
	}
}