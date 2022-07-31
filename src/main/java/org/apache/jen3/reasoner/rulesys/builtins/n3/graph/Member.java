package org.apache.jen3.reasoner.rulesys.builtins.n3.graph;

import java.util.Collections;
import java.util.Iterator;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.SingletonIterator;

public class Member extends GraphBuiltin {

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new MemberTripleSet(clause, definition, graph);
	}

	protected static class MemberTripleSet extends BuiltinTripleSet {

		public MemberTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		public boolean requiresMatching() {
			return true;
		}

		public Iterator<Triple> builtinTriples(Node s, Node o, BindingStack env) {
			Node_CitedFormula graphNode = (Node_CitedFormula) s;

			if (o.isConcrete()) {
				Node_CitedFormula memNode = (Node_CitedFormula) o;

				// in this case, leave it up to the engine machinery to de-construct members
				if (!(memNode.includesVars() || graphNode.includesVars())) {

					StmtIterator stmtIt = memNode.getContents().listStatements();
					// exactly 1 statement
					// TODO check for this in builtins definition
					if (!stmtIt.hasNext())
						return Collections.emptyIterator();
					Statement stmt = stmtIt.next();
					if (stmtIt.hasNext())
						return Collections.emptyIterator();

					if (graphNode.getContents().contains(stmt))
						return new SingletonIterator<Triple>(instantiate(graphNode, memNode));
					else
						return Collections.emptyIterator();
				}
			}

			return new MemberTripleIterator(graphNode);
		}

		protected Triple instantiate(Node graphNode, Node memNode) {
			return new Triple(graphNode, clause.getPredicate(), memNode);
		}

		protected class MemberTripleIterator implements Iterator<Triple> {

			private Node_CitedFormula graphNode;
			private StmtIterator elements;

			public MemberTripleIterator(Node_CitedFormula graphNode) {
				this.graphNode = graphNode;
				elements = graphNode.getContents().listStatements();
			}

			@Override
			public boolean hasNext() {
				return elements.hasNext();
			}

			@Override
			public Triple next() {
				Statement next = elements.next();

				N3ModelSpec spec2 = N3ModelSpec.get(Types.N3_MEM).from(graph.getN3Config());
				N3Model m2 = ModelFactory.createN3Model(spec2);
				m2.add(next);
				Node_CitedFormula cf2 = NodeFactory.createCitedFormula(graph.getScope().sub(Scopes.GRAPH), m2);
				
				return instantiate(graphNode, cf2);
			}
		}
	}
}
