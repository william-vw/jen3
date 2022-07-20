package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.Collections;
import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.SingletonIterator;

public abstract class NotMemberCommon extends ListBuiltin {

	protected static abstract class NotMemberCommonTripleSet extends ListTripleSet {

		public NotMemberCommonTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		public Iterator<Triple> notMemberCommonTriples(Node listNode, Node memNode, BindingStack env) {
			Node_Collection list = (Node_Collection) listNode;
			if (!list.getElements().contains(memNode))
				return new SingletonIterator<Triple>(instantiate(listNode, memNode));
			else
				return Collections.emptyIterator();
		}

		protected abstract Triple instantiate(Node listNode, Node memNode);
	}
}