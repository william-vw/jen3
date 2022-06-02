package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Append extends ListBuiltin {

	public Append() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_Collection list = (Node_Collection) n;
			List<Node> appended = list.getElements().stream().flatMap(l -> ((Node_Collection) l).getElements().stream())
					.collect(Collectors.toList());

			Node_Collection list2 = NodeFactory.createCollection(appended);
			return list2;

		}, null), true);
	}
}
