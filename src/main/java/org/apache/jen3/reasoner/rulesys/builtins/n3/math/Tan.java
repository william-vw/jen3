package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Tan extends MathBuiltin {

	public Tan() {
		super(new MathOnNumbersPattern((n) -> Math.tan(n.doubleValue()), (n) -> Math.atan(n.doubleValue())), true);
	}
}
