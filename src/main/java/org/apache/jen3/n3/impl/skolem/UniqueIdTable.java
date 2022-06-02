package org.apache.jen3.n3.impl.skolem;

import java.util.List;
import java.util.Optional;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

import wvw.utils.map.HashMultiMap;
import wvw.utils.map.MultiMap;

public class UniqueIdTable extends UniqueNodeGen {

	private MultiMap<Node, NodeEntry> map = new HashMultiMap<>();

	public Node uniqueBlankNode(BindingStack env, Node node, Scope scope) {
		List<NodeEntry> entries = map.get(node);

		String id = null;
		if (entries != null) {
			Optional<NodeEntry> entry = entries.stream().filter(e -> e.matches(env)).findAny();

			if (entry.isPresent())
				id = entry.get().getId();
		}
		
		if (id == null) {
			NodeEntry entry = new NodeEntry(env);
			map.putValue(node, entry);

			id = entry.getId();
		}

		return NodeFactory.createBlankNode(id, scope);
	}

	// create a unique id per blank node
//	public Node uniqueSkolem(Node bnode, N3ModelSpec spec) {
//		List<NodeEntry> entries = map.get(bnode);
//
//		NodeEntry entry = null;
//		if (entries == null) {
//			entry = new NodeEntry();
//			map.putValue(bnode, entry);
//
//		} else
//			entry = entries.get(0);
//
//		String id = entry.getId();
//		
//		return NodeFactory.createURI(N3Skolem.uri + id);
//	}

	private class NodeEntry {

		private String id;
		private BindingStack bindings;

		public NodeEntry() {
			id = java.util.UUID.randomUUID().toString();
		}

		public NodeEntry(BindingStack env) {
			this();

			bindings = env.copy();
		}

		public String getId() {
			return id;
		}

		public boolean matches(BindingStack env) {
			return env.equals(bindings);
		}

		@Override
		public String toString() {
			return id + " - " + bindings;
		}
	}
}
