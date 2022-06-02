package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Unique extends ListBuiltin {

	public Unique() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection coll = (Node_Collection) n;
			
			// retains original ordering
			Set<Node> set = new LinkedHashSet<>(coll.getElements());

			return NodeFactory.createCollection(set);

		}, null), true);
	}
}
