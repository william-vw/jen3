package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.FileInputStream;

import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.sys.JenaSystem;

public class N3Test {

	private static String root = "/Users/wvw/git/n3/jen3/testing/N3/";
	
	public static void main(String[] args) throws Exception {
		JenaSystem.init();

		N3ModelSpec spec = N3ModelSpec.get(N3_MEM_FP_INF);

		// configure warnings or errors for common N3 mistakes 
		// (see N3MistakeTypes for a bunch of them!)
		spec.setFeedback(new N3Feedback(N3MistakeTypes.BUILTIN_WRONG_INPUT, FeedbackTypes.WARN,
				FeedbackActions.LOG));
		spec.setFeedback(new N3Feedback(N3MistakeTypes.BUILTIN_UNBOUND_VARS, FeedbackTypes.WARN,
				FeedbackActions.LOG));
		
		// create new N3Model
		N3Model m = ModelFactory.createN3Model(spec);
		m.read(new FileInputStream(root + "jen3_reason/log/includes1.n3"), null);
		
		// get model of deductions
		Model deductions = m.getDeductionsModel();
		// print deductions to system.out
		deductions.write(System.out);
	}
}
