package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;
import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM_FP_INF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.sys.JenaSystem;
import org.apache.jen3.vocabulary.EARL;
import org.apache.jen3.vocabulary.N3Log;
import org.apache.jen3.vocabulary.N3Test;
import org.apache.jen3.vocabulary.RDF;
import org.apache.jen3.vocabulary.TestManifest;

public class N3TestRef extends N3TestCase {

	private static String localRoot = "testing/N3/";

	private static N3ModelSpec spec;

	public static void main(String[] args) throws Exception {
		JenaSystem.init();

		spec = N3ModelSpec.get(N3_MEM_FP_INF);

		// uncomment for recompiling (recompiling is only done when custom builtin path
		// is given)
		spec.getBuiltinConfig()
				.setBuiltinDefPath("src/main/resources/etc/builtins/builtins_illiberal.n3");
		spec.getBuiltinConfig()
				.setBuiltinDefPck("org.apache.jen3.reasoner.rulesys.builtins.n3.def.");

//		spec.setFeedback(new N3Feedback(N3MistakeTypes.INFER_UNBOUND_GLOBALS, FeedbackTypes.WARN,
//				FeedbackActions.LOG));
		spec.setFeedback(new N3Feedback(N3MistakeTypes.BUILTIN_WRONG_INPUT, FeedbackTypes.WARN,
				FeedbackActions.LOG));
		spec.setFeedback(new N3Feedback(N3MistakeTypes.BUILTIN_UNBOUND_VARS, FeedbackTypes.WARN,
				FeedbackActions.LOG));
//		spec.setDownloadTimeout(50);

		// (our own tests)
//		String root = "D:/git/n3dev/jena-core/testing/N3/";
//		String root = "/home/william-vw/projects/n3/jena_n3/testing/N3/";
		String root = "/Users/wvw/git/n3/jen3/testing/N3/";
		// (github repo tests!)
//		String root = "/Users/wvw/git/n3/N3/tests/N3Tests/";
//		String root = "/home/william-vw/projects/n3/william-vw/N3/tests/N3Tests/";

		// - individual tests

		test1(root + "jen3_reason/log/subj_var.n3", null); //, root + "jen3_reason/log/equalto3-ref.n3");

		// - print stats

//		System.out.println("instantiate: " + BindingStack.nrNewGraphs + " - " + BindingStack.nrNewColls);
//		System.out.println("insertVars: " + InsertVarVisitor.nrNewGraphs + " - " + InsertVarVisitor.nrNewColls);

		// - run all tests

		// list all tests from given folder in the manifest
		// (needed when folders are updated with new files)

//		listParserTests(root + "jen3_syntax/", "manifest-parser.ttl", false, root);
//		listReasonTests(root + "jen3_reason/", "manifest-reasoner.ttl", true, root);

		// (add approval for all tests in manifest)
//		addApprov("manifest-reasoner.ttl", "manifest-reasoner-jen3.ttl", root);

		// run tests

//		runParserTests("manifest-parser-jen3.ttl", root);
//		runParserTests("manifest-parser.ttl", root);
//		runReasonTests("manifest-reasoner.ttl", root);
	}

	public static void test(String inPath, String outPath) throws Exception {
		test4(inPath, outPath);
	}

	public static void test1(String inPath, String outPath) throws Exception {
		System.out.println("checking " + inPath);

		String base = "file://" + inPath;

		N3Model m = ModelFactory.createN3Model(spec);
		m.read(new FileInputStream(inPath), base);

		printOutput(m, N3Test.out);
		if (outPath != null)
			checkOutput(m, N3Test.out, outPath);

//		m.getDeductionsModel().write(System.out);
		m.write(System.out);
		System.out.println("");
	}

	public static void test2(String inPath, String outPath) throws Exception {
		String base = TermUtil.getFileUri(new File(inPath).getAbsolutePath());

		N3Model check = ModelFactory.createN3Model(spec);
		check.read(new FileInputStream(localRoot + "test2.n3"), null);

		N3Model actual = ModelFactory.createN3Model(spec);
		actual.read(new FileInputStream(inPath), base);

		Model infs = actual.getDeductionsModel();
		infs.write(System.out);

		CitedFormula af = check.createCitedFormula(spec);
		N3Model afm = af.open();
		infs.getGraph().find().forEachRemaining(t -> afm.add(afm.createStatement(t)));
		af.close();

		Resource a = N3Test.resource("actual");
		check.add(a, N3Test.output, af);

//		printOutput(af.getImmutableContents(), N3Test.out);
		if (outPath != null) {
			CitedFormula ef = check.createCitedFormula(spec);
			ef.open().read(new FileInputStream(outPath), null);
			ef.close();

			Resource e = N3Test.resource("expected");
			check.add(e, N3Test.output, ef);

			System.out.println("checking " + inPath);
			boolean missing = !check.contains(a, N3Test.resource("includes"), e);
			boolean extra = !check.contains(e, N3Test.resource("includes"), a);
			if (extra)
				System.out.println("ERROR: extra output");
			if (missing)
				System.out.println("ERROR: missing output");
			if (!extra && !missing)
				System.out.println("output matches!");
		}

		check.write(System.out);
		System.out.println("");
	}

	public static void test3(String inPath, String outPath) throws Exception {
		N3Model check = ModelFactory.createN3Model(spec);
		check.read(new FileInputStream(localRoot + "test3.n3"), null);

		Resource a = N3Test.resource("actual");
		Resource e = N3Test.resource("expected");

		check.add(a, N3Test.output, check.createLiteral(TermUtil.getFileUri(inPath)));
		if (outPath != null)
			check.add(e, N3Test.output, check.createLiteral(TermUtil.getFileUri(outPath)));

		System.out.println("checking " + inPath);
		boolean missing = !check.contains(a, N3Test.resource("includes"), e);
		boolean extra = !check.contains(e, N3Test.resource("includes"), a);
		if (extra)
			System.out.println("ERROR: extra output");
		if (missing)
			System.out.println("ERROR: missing output");
		if (!extra && !missing)
			System.out.println("output matches!");

		check.write(System.out);
		System.out.println("");
	}

	public static void test4(String inPath, String outPath) throws Exception {
		N3Model check = ModelFactory.createN3Model(spec);
		check.read(new FileInputStream(localRoot + "test4.n3"), null);

		Resource a = check.createResource(N3Test.uri + "actual");
		Resource e = check.createResource(N3Test.uri + "expected");

		check.add(a, N3Test.output, check.createLiteral(TermUtil.getFileUri(inPath)));
		if (outPath != null)
			check.add(e, N3Test.output, check.createLiteral(TermUtil.getFileUri(outPath)));

		System.out.println("checking " + inPath);
		boolean missing = check.contains(a, N3Test.resource("error"),
				N3Test.resource("notEnoughInferences"));
		boolean extra = check.contains(a, N3Test.resource("error"),
				N3Test.resource("tooManyInferences"));
		if (extra)
			System.out.println(
					"ERROR: extra output: " + a.getPropertyResourceValue(N3Test.resource("extra")));
		if (missing)
			System.out.println("ERROR: missing output: "
					+ a.getPropertyResourceValue(N3Test.resource("missing")));
		if (!extra && !missing)
			System.out.println("output matches!");

//		check.getDeductionsModel().write(System.out);
		System.out.println("");
	}

	public static void listReasonTests(String folder, String manifestFile, boolean loadComments,
			String fileRoot) throws Exception {

		listTests(folder, manifestFile, "list_reason_tests.n3", loadComments, fileRoot);
	}

	public static void listParserTests(String folder, String manifestFile, boolean loadComments,
			String fileRoot) throws Exception {

		listTests(folder, manifestFile, "list_parser_tests.n3", loadComments, fileRoot);
	}

	private static void listTests(String folder, String manifestFile, String listFile,
			boolean loadComments, String fileRoot) throws Exception {

		String base = TermUtil.getFileUri(new File(localRoot + "list_tests.n3").getAbsolutePath());

		N3Model list = ModelFactory.createN3Model(spec);
		list.setNsPrefix("test", N3Test.uri);
		list.read(new FileInputStream(localRoot + "testtypes.n3"), null);
		list.read(new FileInputStream(localRoot + "subclass.n3"), null);

		list.read(new FileInputStream(localRoot + "list_tests.n3"), base);
		list.read(new FileInputStream(localRoot + listFile), base);

		Resource tests = list.createResource(base + "#tests");
		list.add(tests, list.createResource(base + "#folder"),
				list.createResource(TermUtil.getFileUri(folder)));
		list.add(tests, list.createResource(base + "#root"),
				list.createResource(TermUtil.getFileUri(fileRoot)));
		list.add(tests, list.createResource(base + "#loadComments"),
				list.createTypedLiteral(loadComments));

//		list.write(System.out);

		N3Model out = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM));
		out.setNsPrefixes(list.getNsPrefixMap());
		list.getDeductionsModel().listStatements()
				.filterDrop(s -> s.getPredicate().equals(N3Log.implies)
						|| s.getPredicate().getNameSpace().startsWith(base))
				.forEachRemaining(s -> out.add(s));

//		out.write(System.out);
		System.out.println("");

		out.write(new FileOutputStream(fileRoot + manifestFile));
		System.out.println("\nwritten manifest to " + (fileRoot + manifestFile));
	}

	public static void runReasonTests(String manifestFile, String fileRoot) throws Exception {
		runTests(manifestFile, "run_reason_tests.n3", fileRoot);
	}

	public static void runParserTests(String manifestFile, String fileRoot) throws Exception {
		runTests(manifestFile, "run_parser_tests.n3", fileRoot);
	}

	private static void runTests(String manifestFile, String runFile, String fileRoot)
			throws Exception {

		long start = System.currentTimeMillis();

		String base = TermUtil.getFileUri(new File(fileRoot + manifestFile).getAbsolutePath());

		N3Model run = ModelFactory.createN3Model(spec);
		run.read(new FileInputStream(localRoot + "testtypes.n3"), null);
		run.read(new FileInputStream(localRoot + "subclass.n3"), null);
		run.read(new FileInputStream(localRoot + runFile), base);
		run.read(new FileInputStream(fileRoot + manifestFile), base);

//		run.getDeductionsModel().write(System.out);
//		run.write(System.out);

		int success = 0;
		int failed = 0;

		Iterator<Resource> testIt = run.listStatements(null, EARL.test, (Resource) null)
				.mapWith(s -> s.getObject());
		while (testIt.hasNext()) {
			Resource test = testIt.next();

			String file = test.getPropertyResourceValue(TestManifest.action).toString();

			StringBuffer error = new StringBuffer();

			StmtIterator assIt = run.listStatements(null, EARL.test, test);
			while (assIt.hasNext()) {
				Statement stmt = assIt.next();

				Resource assertion = stmt.getSubject();
				Resource result = assertion.getPropertyResourceValue(EARL.result);

				Resource outcome = result.getPropertyResourceValue(EARL.outcome);
				if (run.listStatements(outcome, RDF.type, EARL.Fail).hasNext()) {
					error.append("ERROR: ").append(outcome.getLocalName())
							.append(" (" + file + ")\n");

					Resource value = result.getPropertyResourceValue(RDF.value);
					error.append(value).append("\n");

				} else {
					success++;
				}
			}

			if (error.length() > 0) {
				failed++;

				System.out.println("test: " + test.getLocalName());
				System.out.println(error);
			}
//			else 
//				System.out.println("SUCCESS (" + file + ")");

//			System.out.println("");
		}

		System.out.println("total: " + (success + failed));
		System.out.println("success: " + success);
		System.out.println("failed: " + failed);

		long end = System.currentTimeMillis();

		System.out.println("\ntime: " + (end - start));
	}

	public static void addApprov(String manifestFile, String outFile, String fileRoot)
			throws Exception {
		String base = TermUtil.getFileUri(new File(fileRoot + manifestFile).getAbsolutePath());

		N3Model add = ModelFactory.createN3Model(spec);
		add.read(new FileInputStream(localRoot + "testtypes.n3"), base);
		add.read(new FileInputStream(localRoot + "subclass.n3"), base);
		add.read(new FileInputStream(fileRoot + manifestFile), base);
		add.read(new FileInputStream(localRoot + "add_approv.n3"), base);

//		add.write(System.out);

		N3Model out = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM));
		out.setNsPrefixes(add.getNsPrefixMap());
		add.listStatements().filterDrop(s -> s.getPredicate().equals(N3Log.implies))
				.forEachRemaining(s -> out.add(s));

		out.write(System.out);
		System.out.println("");

		out.write(new FileOutputStream(fileRoot + outFile));
		System.out.println("\nwritten manifest to " + (fileRoot + outFile));
	}

	private static Pattern univPat = Pattern.compile("@forAll (.*)\\.");

	public static void replaceUnivQuant(File f) {
		if (f.isDirectory()) {
			for (File f2 : f.listFiles())
				replaceUnivQuant(f2);

		} else {
			try {
				String contents = IOUtils
						.readLines(new FileInputStream(f), Charset.defaultCharset()).stream()
						.collect(Collectors.joining("\n"));

				while (true) {
					Matcher m = univPat.matcher(contents);
					if (m.find()) {
						String vars = m.group(1);
						String[] varNames = vars.split("\\s*,\\s*");

						String rest = contents.substring(m.end());
						for (String varName : varNames) {
							String quickVar = "?" + varName.substring(1);
							rest = rest.replace(varName, quickVar);
						}
						contents = contents.substring(0, m.start()) + rest;

					} else
						break;
				}

				System.out.println(contents);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void testScopes() throws Exception {
		N3Model check = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM));
		check.read(new FileInputStream(localRoot + "reason/nested_clauses.n3"), null);

		System.out.println(check.getScope().printHierarchy());
	}
}