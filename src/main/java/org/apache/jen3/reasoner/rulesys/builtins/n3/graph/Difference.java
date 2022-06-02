package org.apache.jen3.reasoner.rulesys.builtins.n3.graph;

import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet.SingletonTripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil.RuleMatchConfig;

public class Difference extends GraphBuiltin {

	private RuleMatchConfig config = new RuleMatchConfig().bnodesAsWildcards(true);

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new DiffTripleSet(clause, definition, graph);
	}

	private class DiffTripleSet extends SingletonTripleSet {

		public DiffTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		protected Triple builtinTriple(Node sb, Node ob, BindingStack env) {
			Node_Collection list = (Node_Collection) sb;
			Node_CitedFormula graph1 = (Node_CitedFormula) list.getElement(0);
			Node_CitedFormula graph2 = (Node_CitedFormula) list.getElement(1);

//			System.out.println("difference: " + graph1 + " <> " + graph2);

			Node_CitedFormula result = graph1.copy();

			subtractFrom(graph2, result, env);

//			System.out.println("result: " + result);

			if (ob.isConcrete()) {
				if (ob.sameValueAs(result))
					return instantiate(result);
				else
					return null;

			} else
				return instantiate(result);
		}

		protected Triple instantiate(Node_CitedFormula result) {
			return new Triple(clause.getSubject(), clause.getPredicate(), result);
		}
	}

	protected void subtractFrom(Node_CitedFormula graph2, Node_CitedFormula graph1, BindingStack env) {
		env.push();

		Iterator<Triple> it2 = graph2.getContents().getGraph().find();
		Iterator<Triple> it1 = null;
		while (it2.hasNext()) {
			Triple clause2 = it2.next();

			it1 = graph1.getContents().getGraph().find();
			while (it1.hasNext()) {
				Triple clause1 = it1.next();

				boolean bound = RuleUtil.match(clause1, clause2, config, env);
				if (bound)
					it1.remove();
			}
		}

		env.unwind();
	}
}
