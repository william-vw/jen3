package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.Collections;
import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.SingletonIterator;

public abstract class ToLit extends LogBuiltin {

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new ToLitTripleSet(clause, definition, graph);
	}

	protected abstract Node litFromExtra(Node string, Node extra);
	
	protected abstract Node extraFromLit(Node lit);

	private class ToLitTripleSet extends BuiltinTripleSet {

		public ToLitTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env) {
			if (sb.isConcrete()) {
				Node_Collection c = (Node_Collection) sb;

				Node string = c.getElement(0);
				Node extra = c.getElement(1);

				if (string.isConcrete()) {
					Node newLit = litFromExtra(string, extra);

					if (ob.isConcrete()) {
						if (newLit.equals(ob))
							return single(sb, ob);
						else
							return Collections.emptyIterator();

					} else
						return single(sb, newLit);
				}
			}

			Node s2 = NodeFactory.createLiteral(ob.getLiteralValue().toString());
			Node e2 = extraFromLit(ob);

			Node_Collection c2 = NodeFactory.createCollection(s2, e2);

			return single(c2, ob);
		}

		private Iterator<Triple> single(Node subject, Node object) {
			return new SingletonIterator<Triple>(instantiate(subject, object));
		}

		private Triple instantiate(Node subject, Node object) {
			return new Triple(subject, clause.getPredicate(), object);
		}
	}
}