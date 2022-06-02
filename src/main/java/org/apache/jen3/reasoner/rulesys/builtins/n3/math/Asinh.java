package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Asinh extends MathBuiltin {

	public Asinh() {
		super(new MathOnNumbersPattern(
				(n) -> new org.apache.commons.math3.analysis.function.Asinh().value(n.doubleValue()),
				(n) -> Math.sinh(n.doubleValue())), true);
	}
}
