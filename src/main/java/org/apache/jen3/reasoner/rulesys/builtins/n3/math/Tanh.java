package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Tanh extends MathBuiltin {

	public Tanh() {
		super(new MathOnNumbersPattern((n) -> Math.tanh(n.doubleValue()),
				(n) -> new org.apache.commons.math3.analysis.function.Atanh().value(n.doubleValue())), true);
	}
}
