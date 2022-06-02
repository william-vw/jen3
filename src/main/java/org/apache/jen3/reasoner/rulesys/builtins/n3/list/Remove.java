package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Remove extends ListBuiltin {

	public Remove() {
		super(new BinaryFlowPattern((n,g) -> {
			Node_Collection c = (Node_Collection) n;
			Node_Collection list = (Node_Collection) c.getElement(0);
			Node needle = c.getElement(1);

			if (list.hasElement(needle)) {
				List<Node> elements2 = new ArrayList<>(list.getElements());
				do {
					elements2.remove(needle);
				} while (elements2.contains(needle));
				
				return NodeFactory.createCollection(elements2);
			
			} else
				return list;

		}, null), true);
	}
}
