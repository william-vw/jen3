package org.apache.jen3.n3;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.rdf.model.ModelFactory;

public class N3RebindTest {

	public static void main(String[] args) {
		String root = "file:///Users/wvw/git/n3/jena_n3/testing/N3/";
		
		N3Model m = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_FP_INF));
		m.read(root + "jen3_reason/misc/infer-rules.n3");
		
		m.getDeductionsModel().write(System.out);
		
		m.rebind();
		
		m.getDeductionsModel().write(System.out);
	}
}
