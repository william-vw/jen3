package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Degrees extends MathBuiltin {

	public Degrees() {
		super(new MathOnNumbersPattern((n) -> Math.toDegrees(n.doubleValue()), (n) -> Math.toRadians(n.doubleValue())), true);
	}
}