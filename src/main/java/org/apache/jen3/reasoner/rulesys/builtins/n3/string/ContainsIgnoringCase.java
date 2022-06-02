package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class ContainsIgnoringCase extends StringBuiltin {

	public ContainsIgnoringCase() {
		super(new ComparisonPattern(
				(n1, n2) -> Util.parseString(n1).toLowerCase().contains(Util.parseString(n2).toLowerCase())), true);
	}
}