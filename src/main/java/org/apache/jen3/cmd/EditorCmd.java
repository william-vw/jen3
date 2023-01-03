package org.apache.jen3.cmd;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jen3.n3.FeedbackActions;
import org.apache.jen3.n3.FeedbackTypes;
import org.apache.jen3.n3.N3Feedback;
import org.apache.jen3.n3.N3MistakeTypes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.rdf.model.ModelFactory;

public class EditorCmd {

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption(Option.builder("n3").argName("n3").hasArg().desc("input N3 code")
				.numberOfArgs(Option.UNLIMITED_VALUES).valueSeparator(' ').required(true).build());
		options.addOption(Option.builder("inferences").argName("inferences").desc("only return inferences")
				.numberOfArgs(0).required(false).build());
		options.addOption(Option.builder("conclusion").argName("conclusion").desc("return conclusion (closure)")
				.numberOfArgs(0).required(false).build());
		options.addOption(Option.builder("explain").argName("explain").desc("explain SWAP proof").numberOfArgs(0)
				.required(false).build());
		options.addOption(Option.builder("folder").argName("folder").hasArg(true).desc("folder for N3 explain files")
				.numberOfArgs(1).required(false).build());

		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			System.err.println("ERROR: " + exp.getMessage());
			System.exit(1);
		}

		String passedOption = null;
		String[] optionLabels = { "inferences", "conclusion", "explain" };
		String optionsStr = "'" + Arrays.stream(optionLabels).collect(Collectors.joining("', '")) + "'";

		for (String optionLabel : optionLabels) {

			if (line.hasOption(optionLabel)) {
				if (passedOption != null) {
					System.err.println("ERROR: expecting *either* " + optionsStr);
					System.exit(1);

				} else
					passedOption = optionLabel;
			}
		}
		if (passedOption == null) {
			System.err.println("ERROR: expecting " + optionsStr);
			System.exit(1);
		}

		N3Model m = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM_FP_INF));

		String[] paths = line.getOptionValues("n3");
		if (passedOption.equals("inferences") || passedOption.equals("conclusion")) {
			for (String path : paths) {
				InputStream in = new FileInputStream(path);
				m.read(in, null, "N3");
			}

			if (passedOption.equals("inferences"))
				m.getDeductionsModel().write(System.out, "N3");

			else if (passedOption.equals("conclusion"))
				m.write(System.out, "N3");

			System.exit(0);
			
		} else {
			if (!line.hasOption("folder")) {
				System.err.println("ERROR: expecting 'folder' argument when explaining");
				return;
			}
			String folder = line.getOptionValue("folder");

			N3ModelSpec spec = N3ModelSpec.get(Types.N3_MEM_FP_INF);
			spec.setFeedback(
					new N3Feedback(N3MistakeTypes.BUILTIN_STATIC_DATA, FeedbackTypes.NONE, FeedbackActions.NONE));
			N3Model model = ModelFactory.createN3Model(spec);

			String proof = new String(Files.readAllBytes(Paths.get(paths[0])));
			proof = proof.replaceAll("@forAll [^\\{]*", "");

			model.read(new ByteArrayInputStream(proof.getBytes()), "N3");

			model.read(new FileInputStream(folder + "/explain/describe.n3"), "N3");
			model.read(new FileInputStream(folder + "/explain/collect.n3"), "N3");

			model.outputString(System.out);
			System.exit(0);
		}
	}
}
