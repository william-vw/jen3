package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Sort extends ListBuiltin {

	public Sort() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection list = (Node_Collection) n;

			List<Node> elements2 = new ArrayList<>(list.getElements());
			elements2.sort(new Comparator<Node>() {

				@Override
				public int compare(Node n0, Node n1) {
					String s0 = Util.parseString(n0);
					String s1 = Util.parseString(n1);

					return s0.compareTo(s1);
				}
			});

			Node_Collection list2 = NodeFactory.createCollection(elements2);
			return list2;

		}, null), true);
	}
}
