package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Acosh extends MathBuiltin {

	public Acosh() {
		super(new MathOnNumbersPattern(
				(n) -> new org.apache.commons.math3.analysis.function.Acosh().value(n.doubleValue()),
				(n) -> Math.cosh(n.doubleValue())), true);
	}
}
