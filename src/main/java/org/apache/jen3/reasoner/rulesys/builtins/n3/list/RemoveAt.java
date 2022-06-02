package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.ArrayList;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class RemoveAt extends ListBuiltin {

	public RemoveAt() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_Collection c = (Node_Collection) n;
			Node_Collection list = (Node_Collection) c.getElement(0);
			int at = Util.parseInt(c.getElement(1));

			if (at < 0 || at >= list.size())
				return null;

			Node_Collection list2 = NodeFactory.createCollection(new ArrayList<>(list.getElements()));
			list2.removeAt(at);

			return list2;

		}, null), true);
	}
}
