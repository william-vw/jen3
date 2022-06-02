package org.apache.jen3.reasoner.rulesys.builtins.n3;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.sys.JenaSystem;

public class Recompile {

	public static void main(String[] args) {
		JenaSystem.init();

		N3ModelSpec spec = N3ModelSpec.get(N3_MEM_FP_INF);

		// uncomment for recompiling (recompiling is only done when custom builtin path is given)
		spec.getBuiltinConfig()
				.setBuiltinDefPath("src/main/resources/etc/builtins/builtins_illiberal.n3");
		spec.getBuiltinConfig()
				.setBuiltinDefPck("org.apache.jen3.reasoner.rulesys.builtins.n3.def.");

		ModelFactory.createN3Model(spec);
	}
}
