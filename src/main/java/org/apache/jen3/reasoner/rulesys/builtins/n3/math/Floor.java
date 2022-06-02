package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.Util;

public class Floor extends MathBuiltin {

	public Floor() {
		super(new MathOnNumeralsPattern((n) -> Util.floor(n)), true);
	}
}