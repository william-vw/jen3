package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Remainder extends MathBuiltin {

	public Remainder() {
		super(new MathOnNumericListPattern(MathOperations.REMAINDER), true);
	}
}