package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.Util;

public class Ceiling extends MathBuiltin {

	public Ceiling() {
		super(new MathOnNumeralsPattern((n) -> Util.ceiling(n)), true);
	}
}