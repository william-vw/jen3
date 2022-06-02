package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Sinh extends MathBuiltin {

	public Sinh() {
		super(new MathOnNumbersPattern((n) -> Math.sinh(n.doubleValue()),
				(n) -> new org.apache.commons.math3.analysis.function.Asinh().value(n.doubleValue())), true);
	}
}
