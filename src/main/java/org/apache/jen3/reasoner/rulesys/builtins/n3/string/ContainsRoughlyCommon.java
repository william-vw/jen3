package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class ContainsRoughlyCommon extends StringBuiltin {

	public ContainsRoughlyCommon(boolean inverse) {
		super(new ComparisonPattern((n1, n2) -> {
			String s1 = Util.parseString(n1).replaceAll("\\s+", " ");
			String s2 = Util.parseString(n2).replaceAll("\\s+", " ");

			boolean ret = s1.toLowerCase().contains(s2.toLowerCase());
			return (inverse ? !ret : ret);
			
		}), true);
	}
}