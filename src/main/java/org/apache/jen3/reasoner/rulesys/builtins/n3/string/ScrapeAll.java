package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class ScrapeAll extends StringBuiltin {

	public ScrapeAll() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection c = ((Node_Collection) n);
			String str = Util.parseString(c.getElement(0));
			Pattern regex = (Pattern) c.getElement(1).getLiteralValue();

			Node_Collection allScraped = NodeFactory.createCollection();

			Matcher m = regex.matcher(str);
			if (m.groupCount() != 1)
				return null;

			while (m.find()) {
				String scraped = m.group(1);
				allScraped.addElement(NodeFactory.createLiteral(scraped));
			}

			return allScraped;

		}, null), true);
	}

	public static void main(String[] args) throws Exception {
		String str = new String(Files.readAllBytes(new File("src/main/resources/etc/test.txt").toPath()));
		Pattern regex = Pattern.compile("(.+?\\s+.+?\\s+.+?\\s+\\.)");
		
		Matcher m = regex.matcher(str);
		if (m.groupCount() != 1)
			return;

		while (m.find()) {
			String scraped = m.group(1);
			System.out.println(scraped);
		}
	}
}
