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

public abstract class MemberCommon extends ListBuiltin {

	protected abstract static class MemberCommonTripleSet extends ListTripleSet {

		public MemberCommonTripleSet(TriplePattern clause, BuiltinDefinition definition,
				Graph graph) {
			super(clause, definition, graph);
		}

		public Iterator<Triple> memberCommonTriples(Node memNode, Node_Collection listNode,
				BindingStack env) {

			if (memNode.isConcrete()) {
				// in this case, leave it up to the engine machinery to de-construct members
				if (!((memNode.isContainer() && memNode.includesVars())
						|| listNode.includesVars())) {

					if (listNode.getElements().contains(memNode))
						return new SingletonIterator<Triple>(instantiate(memNode, listNode));
					else
						return Collections.emptyIterator();
				}
			}

			return new MemberTripleIterator(listNode);
		}

		protected abstract Triple instantiate(Node memNode, Node listNode);

		protected class MemberTripleIterator implements Iterator<Triple> {

			private Node_Collection listNode;
			private Iterator<Node> elements;

			public MemberTripleIterator(Node_Collection listNode) {
				this.listNode = listNode;
				elements = listNode.getElements().iterator();
			}

			@Override
			public boolean hasNext() {
				return elements.hasNext();
			}

			@Override
			public Triple next() {
				return instantiate(elements.next(), listNode);
			}
		}
	}
}