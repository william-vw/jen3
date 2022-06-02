package org.apache.jen3.reasoner.rulesys;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.mem.Selectivity;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.ContinuationOnFailureIterator;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jen3.util.iterator.TripleForMatchIterator;

public class PatternTripleSet extends TripleSet {

	private Graph graph;
	private Finder continuation;

	public PatternTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		super(clause);

		this.graph = graph;
		this.continuation = continuation;
	}

	@Override
	public boolean requiresMatching() {
		return true;
	}

	@Override
	public TripleForMatchIterator triples(BindingStack env) {
		return find(graph, clause, env);
	}

	private TripleForMatchIterator find(Graph graph, TriplePattern clause, BindingStack env) {
		TriplePattern bound = new TriplePattern(env.groundShallow(clause.getSubject()),
				env.groundShallow(clause.getPredicate()), env.groundShallow(clause.getObject()));

		ExtendedIterator<Triple> ret = null;

		byte idx = Selectivity.chooseIndex(bound, false);
		if (idx > 2)
			ret = graph.find();

		// only fully ground node at the chosen position
		else {
			Node n = env.ground(clause.get(idx));
//			System.out.println("needle? " + n + " - " + graph.getClass());

			switch (idx) {

			case Triple.S:
				ret = graph.find(n, Node.ANY, Node.ANY);
				break;

			case Triple.P:
				ret = graph.find(Node.ANY, n, Node.ANY);
				break;

			case Triple.O:
				ret = graph.find(Node.ANY, Node.ANY, n);
				break;

			default:
				ret = graph.find();
				break;
			}
		}

//		System.out.println("searching? " + idx + " - " + ret.hasNext() + " - " + bound);

		return new ContinuationOnFailureIterator(ret, (v) -> continuation.find(bound));
	}

	@Override
	public String toString() {
		return "<PatternTripleSet:  " + clause + ">";
	}
}