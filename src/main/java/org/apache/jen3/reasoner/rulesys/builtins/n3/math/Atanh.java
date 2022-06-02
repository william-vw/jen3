package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Atanh extends MathBuiltin {

	public Atanh() {
		super(new MathOnNumbersPattern(
				(n) -> new org.apache.commons.math3.analysis.function.Atanh().value(n.doubleValue()),
				(n) -> Math.tanh(n.doubleValue())), true);
	}
}
