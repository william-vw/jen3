package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Cosh extends MathBuiltin {

	public Cosh() {
		super(new MathOnNumbersPattern((n) -> Math.cosh(n.doubleValue()),
				(n) -> new org.apache.commons.math3.analysis.function.Acosh().value(n.doubleValue())), true);
	}
}
