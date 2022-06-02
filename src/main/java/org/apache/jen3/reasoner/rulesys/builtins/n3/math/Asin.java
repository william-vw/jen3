package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Asin extends MathBuiltin {

	public Asin() {
		super(new MathOnNumbersPattern((n) -> Math.asin(n.doubleValue()), (n) -> Math.sin(n.doubleValue())), true);
	}
}
