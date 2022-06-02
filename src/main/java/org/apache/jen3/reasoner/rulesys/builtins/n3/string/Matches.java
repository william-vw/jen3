package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import java.util.regex.Pattern;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class Matches extends StringBuiltin {

	public Matches() {
		super(new ComparisonPattern((n1, n2) -> {
			String str = Util.parseString(n1);
			Pattern regex = (Pattern) n2.getLiteralValue();

			return regex.matcher(str).find();
		}), true);
	}
}
