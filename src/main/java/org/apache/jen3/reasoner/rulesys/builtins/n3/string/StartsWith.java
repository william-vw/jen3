package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class StartsWith extends StringBuiltin {

	public StartsWith() {
		super(new ComparisonPattern((n1, n2) -> Util.parseString(n1).startsWith(Util.parseString(n2))), true);
	}
}
