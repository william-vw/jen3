package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Exponentiation extends MathBuiltin {

	public Exponentiation() {
		super(new MathOnNumericListPattern(MathOperations.EXPONENTIATION), true);
	}
}
