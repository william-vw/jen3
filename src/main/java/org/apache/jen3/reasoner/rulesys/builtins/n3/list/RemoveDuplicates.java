package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class RemoveDuplicates extends ListBuiltin {

	public RemoveDuplicates() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection c = (Node_Collection) n;

			List<Node> elements2 = new ArrayList<>(new LinkedHashSet<>(c.getElements()));
			return NodeFactory.createCollection(elements2);

		}, null), true);
	}
}
