package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Cos extends MathBuiltin {

	public Cos() {
		super(new MathOnNumbersPattern((n) -> Math.cos(n.doubleValue()), (n) -> Math.acos(n.doubleValue())), true);
	}
}
