package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class Contains extends StringBuiltin {

	public Contains() {
		super(new ComparisonPattern((n1, n2) -> 
			Util.parseString(n1).contains(Util.parseString(n2))
		
		), true);
	}
}
