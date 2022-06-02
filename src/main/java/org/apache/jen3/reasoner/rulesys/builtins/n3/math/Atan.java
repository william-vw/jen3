package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Atan extends MathBuiltin {

	public Atan() {
		super(new MathOnNumbersPattern((n) -> Math.atan(n.doubleValue()), (n) -> Math.tan(n.doubleValue())), true);
	}
}
