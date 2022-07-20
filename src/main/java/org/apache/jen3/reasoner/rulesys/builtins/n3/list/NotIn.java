package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

public class NotIn extends NotMemberCommon {

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new NotInTripleSet(clause, definition, graph);
	}

	private static class NotInTripleSet extends NotMemberCommonTripleSet {

		public NotInTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env) {
			return notMemberCommonTriples(ob, sb, env);
		}

		@Override
		protected Triple instantiate(Node listNode, Node memNode) {
			return new Triple(memNode, clause.getPredicate(), listNode);
		}
	}
}
