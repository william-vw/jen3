package org.apache.jen3.cmd;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;

public class PrintN3Scopes {

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption("n3", true, "N3 code");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		String n3 = cmd.getOptionValue("n3");

		N3Model m = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM));
		m.read(new ByteArrayInputStream(n3.getBytes()), null);

		PrintStream out = System.out;
		out.println("- scope hierarchy:");
		out.println(m.getScope().printHierarchy());

		out.println("\n");

		out.println("N3 (printed from model):");
		m.write(out);
	}
}
