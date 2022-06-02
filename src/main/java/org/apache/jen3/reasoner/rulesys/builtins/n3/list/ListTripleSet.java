package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public abstract class ListTripleSet extends BuiltinTripleSet {

	public ListTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
		super(clause, definition, graph);
	}
	
	@Override
	public boolean requiresMatching() {
		return true;
	}
}
