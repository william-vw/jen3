package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import org.apache.jen3.graph.n3.N3CitedGraph;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;

public class Inferences extends LogBuiltin {

	public Inferences() {
		super(new ReasoningFlowPattern((m) -> {
			Model infs = m.getDeductionsModel();
			
			N3ModelSpec spec = N3ModelSpec.get(N3_MEM);
			
			N3Model m2 = ModelFactory.createN3Model(spec, new N3CitedGraph());
			infs.getGraph().find().forEachRemaining(s -> { m2.add(m2.createStatement(s)); });
			
			return m2;

		}), true);
	}
}
