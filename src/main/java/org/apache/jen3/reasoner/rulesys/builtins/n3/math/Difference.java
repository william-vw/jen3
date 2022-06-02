package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Difference extends MathBuiltin {

	public Difference() {
		super(new MathOnNumericListPattern(MathOperations.SUBTRACT), true);
	}
}
