package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.SingletonIterator;

public class Iterate extends ListBuiltin {

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new IterateTripleSet(clause, definition, graph);
	}

	private class IterateTripleSet extends ListTripleSet {

		public IterateTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env) {
			Node_Collection listNode = (Node_Collection) sb;

			if (ob.isConcrete()) {
				Node_Collection c2 = (Node_Collection) ob;
				Node idxNode = c2.getElement(0);
				Node memNode = c2.getElement(1);

				if (memNode.isConcrete()) {
					// in this case, leave it up to the engine machinery to de-construct members
					if (!memNode.isContainer() || !memNode.includesVars()) {
						// a node may have multiple indexes
						List<Integer> memIdxs = IntStream.range(0, listNode.getElements().size())
								.filter(i -> listNode.getElement(i).equals(memNode))
								.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

						if (idxNode.isConcrete()) {
							int idx = Util.parseInt(idxNode);
							if (memIdxs.contains(idx))
								return new SingletonIterator<Triple>(instantiate(listNode, idxNode, memNode));
						} else
							return new IterateTheseTriples(listNode, memIdxs, memNode);
					
					} else
						return new IterateAllTriples(listNode);

				} else {
					if (idxNode.isConcrete()) {
						int idx = Util.parseInt(idxNode);

						if (idx >= 0 && idx < listNode.size()) {
							Node memNode2 = listNode.getElement(idx);
							return new SingletonIterator<Triple>(instantiate(listNode, idxNode, memNode2));
						}

					} else
						return new IterateAllTriples(listNode);
				}

			} else
				return new IterateAllTriples(listNode);

			return Collections.emptyIterator();
		}

		private Triple instantiate(Node listNode, int idx, Node memNode) {
			return instantiate(listNode, NodeFactory.createLiteralByValue(idx, XSDDatatype.XSDint), memNode);
		}

		private Triple instantiate(Node listNode, Node idxNode, Node memNode) {
			// System.out.println("instantiate " + idxNode + " - " + memNode);

			Node_Collection obj = NodeFactory.createCollection();
			obj.addElement(idxNode);
			obj.addElement(memNode);

			return new Triple(listNode, clause.getPredicate(), obj);
		}

		private abstract class IterateTriples implements Iterator<Triple> {

			protected int cnt = 0;
			protected Node listNode;

			public IterateTriples(Node listNode) {
				this.listNode = listNode;
			}
		}

		private class IterateAllTriples extends IterateTriples {

			private Iterator<Node> elements;

			public IterateAllTriples(Node_Collection listNode) {
				super(listNode);

				elements = listNode.getElements().iterator();
			}

			@Override
			public boolean hasNext() {
				return elements.hasNext();
			}

			@Override
			public Triple next() {
				return instantiate(listNode, cnt++, elements.next());
			}
		}

		private class IterateTheseTriples extends IterateTriples {

			private List<Integer> idxs;
			private Node memNode;

			public IterateTheseTriples(Node_Collection listNode, List<Integer> idxs, Node memNode) {
				super(listNode);

				this.idxs = idxs;
				this.memNode = memNode;
			}

			@Override
			public boolean hasNext() {
				return cnt < idxs.size();
			}

			@Override
			public Triple next() {
				return instantiate(listNode, idxs.get(cnt++), memNode);
			}
		}
	}
}
