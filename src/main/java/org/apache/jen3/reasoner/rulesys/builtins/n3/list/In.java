package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

public class In extends MemberCommon {

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new InTripleSet(clause, definition, graph);
	}

	private static class InTripleSet extends MemberCommonTripleSet {

		public InTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public Iterator<Triple> builtinTriples(Node memNode, Node ob, BindingStack env) {
			return memberCommonTriples(memNode, (Node_Collection) ob, env);
		}

		@Override
		protected Triple instantiate(Node memNode, Node listNode) {
			return new Triple(memNode, clause.getPredicate(), listNode);
		}
	}
}