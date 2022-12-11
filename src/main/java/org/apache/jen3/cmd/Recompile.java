package org.apache.jen3.cmd;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.FileInputStream;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.sys.JenaSystem;

public class Recompile {

	public static void main(String[] args) throws Exception {
		JenaSystem.init();

		N3ModelSpec spec = N3ModelSpec.get(N3_MEM_FP_INF);

		// (recompiling is only done when custom builtin path is given)
		spec.getBuiltinConfig().setBuiltinDefPath("src/main/resources/etc/builtins/builtins_illiberal.n3");
		spec.getBuiltinConfig().setBuiltinDefPck("org.apache.jen3.reasoner.rulesys.builtins.n3.def.");

		N3Model check = ModelFactory.createN3Model(spec);

		// (if you wanna quickly check whether the updated definitions are working!)
		check.read(new FileInputStream(
				"/Users/wvw/git/n3/code_gen/tool/code-gen/src/main/resources/rheumatology/cmd_test.n3"), null);
		check.write(System.out);
	}
}
