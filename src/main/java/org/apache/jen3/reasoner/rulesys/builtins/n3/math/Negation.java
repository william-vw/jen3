package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.Util;

public class Negation extends MathBuiltin {

	public Negation() {
		super(new MathOnNumeralsPattern((n) -> Util.negation(n), (n) -> Util.negation(n)), true);
	}
}
