package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

public class Sin extends MathBuiltin {

	public Sin() {
		super(new MathOnNumbersPattern((n) -> Math.sin(n.doubleValue()), (n) -> Math.asin(n.doubleValue())), true);
	}
}
