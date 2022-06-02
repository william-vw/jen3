package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.Util;

public class AbsoluteValue extends MathBuiltin {

	public AbsoluteValue() {
		super(new MathOnNumeralsPattern((n) -> Util.absoluteValue(n)), true);
	}
}
