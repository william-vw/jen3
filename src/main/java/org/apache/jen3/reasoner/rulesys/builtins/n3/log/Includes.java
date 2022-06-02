package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.N3CitedGraph;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;
import org.apache.jen3.util.iterator.SingletonIterator;
import org.apache.jen3.vocabulary.N3Log;

public class Includes extends LogInclude {

	public Includes() {
		super(false);
	}

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new IncludesTripleSet(clause, definition, graph);
	}

	private class IncludesTripleSet extends BuiltinTripleSet {

		private Node predicate = NodeFactory.createURI(N3Log.uri + "includes");

		public IncludesTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public Iterator<Triple> builtinTriples(Node sb, Node ob, BindingStack env) {
			// keep it simple if there aren't any variables!
			if (!(sb.includesRuleVars() || ob.includesRuleVars())) {
				if (match(sb, ob, env, graph))
					// no matching will take place afterwards in rule engine
					// (requiresMatching is false per default)
					return new SingletonIterator<Triple>(clause.asTriple());
				else
					return Collections.emptyIterator();
			}

			List<Triple> subs = getClauses(ob, graph);
			List<Triple> sups = getClauses(sb, graph);
			List<List<Triple>> subGraphs = new ArrayList<>();

			// find suitable subgraphs here
			// TODO doing duplicate work here as successful bindings are discarded ..
			
			RuleUtil.matchSubGraph(subs, 0, sups, env, subGraphs, new ArrayList<>());
			return new IncludesTripleIterator(sb, subGraphs);
		}

		private class IncludesTripleIterator implements Iterator<Triple> {

			private Node sb;
			private Iterator<List<Triple>> subGraphs;

			public IncludesTripleIterator(Node sb, List<List<Triple>> subGraphs) {
				this.sb = sb;
				this.subGraphs = subGraphs.iterator();
			}

			@Override
			public boolean hasNext() {
				return subGraphs.hasNext();
			}

			@Override
			public Triple next() {
				List<Triple> subGraph = subGraphs.next();

				N3Model m = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM), new N3CitedGraph(subGraph));
				Node_CitedFormula cf = NodeFactory.createCitedFormula(graph.getScope(), m);

				return new Triple(sb, predicate, cf);
			}
		}
	}
}