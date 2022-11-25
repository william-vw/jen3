package org.apache.jen3.cmd;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.N3ModelSpec.Types;
import org.apache.jen3.rdf.model.ModelFactory;

public class PrettyPrintCmd {

	// example:
	// -n3 /Users/wvw/git/n3/jen3/testing/N3/jen3_reason/list/list1.n3
	
	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption(Option.builder("n3").argName("n3").hasArg().desc("input N3 code")
				.numberOfArgs(Option.UNLIMITED_VALUES).valueSeparator(' ').required(true).build());

		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);

		} catch (ParseException exp) {
			System.err.println("ERROR: " + exp.getMessage());
			System.exit(1);
		}

		N3Model m = ModelFactory.createN3Model(N3ModelSpec.get(Types.N3_MEM));

		String[] paths = line.getOptionValues("n3");
		for (String path : paths) {
			InputStream in = new FileInputStream(path);
			m.read(in, null, "N3");
		}
		
		m.write(System.out);
	}
}
