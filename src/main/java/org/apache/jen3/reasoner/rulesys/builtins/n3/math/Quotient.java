package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Quotient extends MathBuiltin {

	public Quotient() {
		super(new MathOnNumericListPattern(MathOperations.DIVIDE), true);
	}
}

