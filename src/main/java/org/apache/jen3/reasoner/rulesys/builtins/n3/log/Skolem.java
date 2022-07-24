package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.n3.impl.skolem.UniqueIdViaKey;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Skolem extends LogBuiltin {

	private static String ns = "http://www.w3.org/2000/10/swap/genid#";
	
	public Skolem() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection coll = (Node_Collection) n;
			return gen(coll.getElements());

		}, null), true);
	}
	
	public static Node gen(Node ... bindings) {
		String id = UniqueIdViaKey.uniqueId(bindings);
		return NodeFactory.createURI(ns + id);
	}
	
	public static Node gen(List<Node> bindings) {
		String id = UniqueIdViaKey.uniqueId(bindings);
		return NodeFactory.createURI(ns + id);
	}
}
