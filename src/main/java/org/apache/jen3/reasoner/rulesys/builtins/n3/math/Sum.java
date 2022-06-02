package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Sum extends MathBuiltin {

	public Sum() {
		super(new MathOnNumericListPattern(MathOperations.ADD), true);
	}
}
