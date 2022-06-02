package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;

public class N3BuiltinDefTest {

	public static void main(String[] args) throws FileNotFoundException {
		N3ModelSpec spec1 = N3ModelSpec.get(N3_MEM_FP_INF);
		spec1.getBuiltinConfig().setBuiltinDefPath("etc/builtins/builtins_illiberal.n3");
		N3Model m1 = ModelFactory.createN3Model(spec1);

		N3ModelSpec spec2 = N3ModelSpec.get(N3_MEM_FP_INF);
		spec2.getBuiltinConfig().setBuiltinDefPath("etc/builtins/builtins_liberal.n3");
		N3Model m2 = ModelFactory.createN3Model(spec2);
		
		m2.read(new FileInputStream("testing/N3/jen3_reason/misc/builtin_liberal.n3"), null);
		m2.write(System.out);
		
		N3ModelSpec spec3 = N3ModelSpec.get(N3_MEM_FP_INF);
		spec1.getBuiltinConfig().setBuiltinDefPath("etc/builtins/builtins_illiberal.n3");
		N3Model m3 = ModelFactory.createN3Model(spec3);
		
		m3.read(new FileInputStream("testing/N3/jen3_reason/misc/builtin_liberal.n3"), null);
		m3.write(System.out);
	}
}
