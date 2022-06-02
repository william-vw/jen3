package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class NotEqualIgnoringCase extends StringBuiltin {

	public NotEqualIgnoringCase() {
		super(new ComparisonPattern((n1, n2) -> !Util.parseString(n1).equalsIgnoreCase(Util.parseString(n2))), true);
	}
}
