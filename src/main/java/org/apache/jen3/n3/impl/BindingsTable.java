package org.apache.jen3.n3.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

import wvw.utils.map.HashMultiMap;
import wvw.utils.map.MultiMap;

public class BindingsTable {

	private MultiMap<Node, BindingStack> map = new HashMultiMap<>();

	public void add(BindingStack env) {
		Node idx = findIndex(env);
		map.putValue(idx, env);
	}

	public boolean checkAndAdd(BindingStack env) {
		Node idx = findIndex(env);
		if (idx == null)
			return false;

		List<BindingStack> entries = map.get(idx);
		if (entries != null) {
			if (entries.stream().anyMatch(e -> e.equals(env)))
				return true;
		}

		map.putValue(idx, env.copy());
		return false;
	}

	private Node findIndex(BindingStack env) {
		Optional<Node> idx = Arrays.stream(env.getEnvironment()).filter(n -> n != null).findFirst();

		return (idx.isPresent() ? idx.get() : null);
	}

	@Override
	public String toString() {
		if (map.isEmpty())
			return "<empty>";
		else
			return "[\n    " + map.values().stream().flatMap(l -> l.stream()).map(l -> l.toString())
					.collect(Collectors.joining("\n    ")) + "\n]";
	}
}
