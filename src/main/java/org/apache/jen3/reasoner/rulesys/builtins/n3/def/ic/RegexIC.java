package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.impl.AdhocDatatype;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class RegexIC extends InputConstraint {

	private static final long serialVersionUID = 5326047120388171817L;
	
	private static Pattern ciRegexPattern = Pattern.compile("/(.*)/(i?)");
	private static RDFDatatype regexType = new AdhocDatatype(Pattern.class);

	public RegexIC() {
		super(DefaultICs.REGEX);
	}

	@Override
	public boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		if (Util.isStringable(n)) {
			String str = Util.parseString(n);
			try {
				return compilePattern(str) != null;

			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		} else
			return false;
	}

	// don't do separate check (avoid doing work twice)
	// instead, just do all work in one go, incl. checking

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (Util.isStringable(n)) {
			String str = Util.parseString(n);
			try {
				Pattern p = compilePattern(str);
				if (p == null)
					return noMatch;

				return new ICConvert(NodeFactory.createLiteralByValue(p, regexType));

			} catch (Exception e) {
				e.printStackTrace();
				return noMatch;
			}
		}

		return noMatch;
	}

	protected Pattern compilePattern(String regex) {
		regex = regex.replace("\\\\", "\\");
		Matcher m = ciRegexPattern.matcher(regex);
		if (m.matches()) {
			regex = m.group(1).replace("\\\\", "\\");

			boolean ci = !m.group(2).trim().isEmpty();
			return Pattern.compile(regex, (ci ? Pattern.CASE_INSENSITIVE : 0) | Pattern.DOTALL);

		} else {
			return Pattern.compile(regex, Pattern.DOTALL);
		}
	}
}
