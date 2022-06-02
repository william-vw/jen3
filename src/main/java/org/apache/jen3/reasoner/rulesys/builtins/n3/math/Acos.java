package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Acos extends MathBuiltin {

	public Acos() {
		super(new MathOnNumbersPattern((n) -> Math.acos(n.doubleValue()), (n) -> Math.cos(n.doubleValue())), true);
	}
}
