package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;

public class N3BNodeGenTest {

	// turned out to be a bit silly, this one
	// no noticeable difference for small formulas
	// (this will likely change with bigger cited formulas)

	public static void main(String[] args) throws IOException {
		long total = 0;

		// see N3Rule#uniqueBlankNode()
		// use different BNodeGenerator subclasses to compare performance

		for (int i = 0; i < 100; i++) {
			System.out.println("iteration " + i);

			long start = System.currentTimeMillis();
			N3Model m = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM_FP_INF));
			m.read(new FileInputStream("testing/N3/jen3_reason/misc/gen-bnodes.n3"), null);
			m.prepare();
			long end = System.currentTimeMillis();

			total += (end - start);
		}

		System.out.println("total: " + total);

		// skolemizer: 846ms
		// bnodetable: 848ms
	}
}
