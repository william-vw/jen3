package org.apache.jen3.n3;

import java.util.List;

import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.rdf.model.InfModel;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.reasoner.Reasoner;
import org.apache.jen3.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.vocabulary.N3;

public class TestLP {

	public static void main(String[] args) {
//		testNative();
//		testN3LP();
		testN3Hybrid();
	}

	public static void testNative() {
		N3Model rawData = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM));
		rawData.read("file:///Users/wvw/git/n3/jen3/testing/N3/lp/data.ttl", null, "N3");

		List<Rule> rules = Rule.rulesFromURL("file:///Users/wvw/git/n3/jen3/testing/N3/lp/lp.jena");
		Reasoner reasoner = new GenericRuleReasoner(rules);
		InfModel inf = ModelFactory.createInfModel(reasoner, rawData);

		StmtIterator it = inf.listStatements(inf.createResource("http://example.org/x"), null,
				(Resource) null);

		while (it.hasNext())
			System.out.println(it.next());
	}

	public static void testN3LP() {
		N3Model data = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_LP_INF));
		data.read("file:///Users/wvw/git/n3/jen3/testing/N3/lp/lp.n3", null, "N3");

		StmtIterator it = data.listStatements(data.createResource("http://example.org/x"), null,
				(Resource) null);

		while (it.hasNext())
			System.out.println(it.next());
	}

	public static void testN3Hybrid() {
		N3Model data = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_HYBRID_INF));
		data.read("file:///Users/wvw/git/n3/jen3/testing/N3/lp/hybrid1.1.n3", null, "N3");

		System.out.println("\npreprocessed:");
		data.getDeductionsModel().write(System.out);

		N3Model data2 = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_HYBRID_INF));

		data2.add(data.listStatements());

		data2.read("file:///Users/wvw/git/n3/jen3/testing/N3/lp/hybrid1.2.n3", null, "N3");

		StmtIterator it2 = data2.listStatements(
				data2.createResource("http://niche.cs.dal.ca/ns/cig/rbc_match.owl#taskA"), null,
				(Resource) null);

		System.out.println("\ninferred:");
		while (it2.hasNext())
			System.out.println(it2.next());
	}
}
