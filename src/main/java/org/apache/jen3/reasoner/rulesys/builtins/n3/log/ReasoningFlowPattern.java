package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.util.function.Function;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.NodeVisitor;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.InsertDataVarVisitor;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class ReasoningFlowPattern extends BinaryFlowPattern {

	public ReasoningFlowPattern(Function<N3Model, N3Model> genOutput) {
		build(new DefaultFlowVertex(new ReasoningFlowEdge(genOutput)),
				new DefaultFlowVertex(new DefaultFlowEdge(null)));
	}

	private class ReasoningFlowEdge extends DefaultFlowEdge {

		private Function<N3Model, N3Model> genOutput;

		public ReasoningFlowEdge(Function<N3Model, N3Model> getModel) {
			this.genOutput = getModel;
		}

		@Override
		protected Node flow(Node n, Graph g) {
			InsertDataVarVisitor varVisit = new InsertDataVarVisitor();

			Node_CitedFormula f = (Node_CitedFormula) n;
			Graph graph = f.getContents().getGraph();

			N3Model m2 = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM_FP_INF));
			Util.baseOn(m2, f.getContents());
			
			graph.find().forEachRemaining(t -> m2.add(toSafeStmt(m2, t, varVisit)));

			N3Model m3 = genOutput.apply(m2);

			return NodeFactory.createCitedFormula(f.getScope().leveled(Scopes.GRAPH), m3);
		}
	}

	private static Statement toSafeStmt(N3Model m, Triple t, NodeVisitor visitor) {
		Node s = t.getSubject().visitWith(visitor, null);
		Node p = t.getPredicate().visitWith(visitor, null);
		Node o = t.getObject().visitWith(visitor, null);

		if (s != t.getSubject() || p != t.getPredicate() || o != t.getObject())
			t = new Triple(s, p, o);

		return m.createStatement(t);
	}
}
