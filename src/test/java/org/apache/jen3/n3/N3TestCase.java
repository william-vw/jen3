package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.io.N3JenaWriterFull;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jena.atlas.logging.Log;

import junit.framework.TestCase;

public class N3TestCase extends TestCase {

	protected static void printOutput(N3Model m, Resource predicate) {
		System.out.println("output:");

		N3JenaWriterFull writer = getWriter();
		List<String> out = stmtsToCanon(m.listStatements(null, predicate, (Resource) null), writer);

		out.sort(null);
		out.stream().forEach(s -> System.out.println(s));

		System.out.println();
	}

	protected static void checkOutput(N3Model m, Resource predicate, String outputFile) throws IOException {
		System.out.println("checking output");
		
		N3Model out = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM));
		out.read(new FileInputStream(outputFile), null);

		List<String> stmts = stmtsToCanon(m.listStatements(null, predicate, (Resource) null), getWriter());
		List<String> stmts2 = stmtsToCanon(out.listStatements(null, predicate, (Resource) null), getWriter());

		String ref = "[" + outputFile + "] ";

		boolean error = false;
		for (String stmt : stmts) {
			if (!stmts2.contains(stmt)) {
				error = true;
				Log.error(N3TestCase.class, ref + "extra output: " + stmt);
			}
		}
		for (String stmt2 : stmts2) {
			if (!stmts.contains(stmt2)) {
				error = true;
				Log.error(N3TestCase.class, ref + "missing output: " + stmt2);
			}
		}

		if (!error)
			System.out.println("output matches!");
	}

	private static N3JenaWriterFull getWriter() {
		N3JenaWriterFull writer = new N3JenaWriterFull(false);
		return writer;
	}

	private static List<String> stmtsToCanon(StmtIterator it, N3JenaWriterFull writer) {
		return it.toList().stream().map(stmt -> stmtToCanon(stmt, writer)).collect(Collectors.toList());
	}

	private static String stmtToCanon(Statement stmt, N3JenaWriterFull writer) {
		return writer.formatNode(stmt.getSubject()) + " " + writer.formatNode(stmt.getPredicate()) + " "
				+ writer.formatNode(stmt.getObject()) + " .";
	}
}
