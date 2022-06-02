package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Logarithm extends MathBuiltin {

	public Logarithm() {
		super(new MathOnNumericListPattern(MathOperations.LOGARITHM), true);
	}
}