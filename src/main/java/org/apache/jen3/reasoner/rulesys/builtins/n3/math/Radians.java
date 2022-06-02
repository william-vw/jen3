package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Radians extends MathBuiltin {

	public Radians() {
		super(new MathOnNumbersPattern((n) -> Math.toRadians(n.doubleValue()), (n) -> Math.toDegrees(n.doubleValue())),
				true);
	}
}