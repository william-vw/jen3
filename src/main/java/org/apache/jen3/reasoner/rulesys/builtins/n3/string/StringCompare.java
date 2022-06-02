package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public abstract class StringCompare extends StringBuiltin {

	public StringCompare(int cmpValue, boolean inverse) {
		super(new ComparisonPattern((n1, n2) -> {
			int cmp = Util.parseString(n1).compareTo(Util.parseString(n2));
			if (inverse)
				return cmp != cmpValue;
			else
				return cmp == cmpValue;
			
		}), true);
	}
}
