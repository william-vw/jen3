package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.util.function.Function;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.N3CitedGraph;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.n3.FeedbackTypes;
import org.apache.jen3.n3.N3Feedback;
import org.apache.jen3.n3.N3MistakeTypes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.N3Feedback.N3FeedbackListener;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;
import org.apache.jena.atlas.logging.Log;

public class SemanticsFlowPattern extends BinaryFlowPattern {

	public SemanticsFlowPattern(Function<String, Node> onError) {
		build(new DefaultFlowVertex(new SemanticsFlowEdge(onError)), new DefaultFlowVertex(new DefaultFlowEdge(null)));
	}

	private class SemanticsFlowEdge extends DefaultFlowEdge implements N3FeedbackListener {

		private String error;
		private Function<String, Node> onError;

		public SemanticsFlowEdge(Function<String, Node> onError) {
			this.onError = onError;
		}

		@Override
		protected Node flow(Node n, Graph g) {
			this.error = null;

			N3ModelSpec spec = N3ModelSpec.get(N3_MEM);
			spec.setFeedback(new N3Feedback(N3MistakeTypes.ALL, this));

			N3Model m2 = ModelFactory.createN3Model(spec, new N3CitedGraph());
			m2.getGraph().setConfig(g.getConfig());

			if (g.getN3Config().getBuiltinConfig().isPrintDownloads())
				System.out.println("[log:semantics] getting: " + n.getURI());
			try {
				Util.retrieveN3(n.getURI(), m2);

			} catch (Exception e) {
//				e.printStackTrace();

				error = "[ERROR] " + ExceptionUtils.getRootCauseMessage(e);
				Log.error(getClass(), error);

				return onError.apply(error);
			}

			Node_CitedFormula cf = NodeFactory.createCitedFormula(g.getScope().sub(Scopes.GRAPH), m2);
			if (error != null)
				return onError.apply(error);

			return cf;
		}

		@Override
		public void onFeedback(N3Feedback feedback, String msg) {
			if (feedback.getType() == FeedbackTypes.ERROR) {
				Log.error(getClass(), msg);

				// record first error
				if (error == null)
					error = msg;
			}
		}
	}
}