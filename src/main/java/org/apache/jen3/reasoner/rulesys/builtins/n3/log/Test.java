package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.SingletonIterator;

public class Test extends LogBuiltin {

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new BuiltinTripleSet(clause, definition, graph) {
			@Override
			public Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env) {
				return new SingletonIterator<>(clause.asTriple());
			}
		};
	}
}
