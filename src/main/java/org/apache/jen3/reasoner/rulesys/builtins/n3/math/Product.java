package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;

public class Product extends MathBuiltin {

	public Product() {
		super(new MathOnNumericListPattern(MathOperations.MULTIPLY), true);
	}
}