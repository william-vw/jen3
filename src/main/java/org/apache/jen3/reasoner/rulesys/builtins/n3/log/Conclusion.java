package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import org.apache.jen3.graph.n3.N3CitedGraph;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;

public class Conclusion extends LogBuiltin {

	public Conclusion() {
		super(new ReasoningFlowPattern((m) -> {
			// TODO don't want an inferencing model to be returned so copy contents here :-(
			// (inf. model only useful for log:conclusion, log:inferences)

			N3Model m2 = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM), new N3CitedGraph());
			m.getGraph().find().forEachRemaining(s -> m2.add(m2.createStatement(s)));

			return m2;

		}), true);
	}
}
