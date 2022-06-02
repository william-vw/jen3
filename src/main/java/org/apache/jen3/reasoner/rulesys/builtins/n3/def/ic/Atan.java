package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.MathBuiltin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.math.MathOnNumbersPattern;

public class Atan extends MathBuiltin {

	public Atan() {
		super(new MathOnNumbersPattern((n) -> Math.atan(n.doubleValue()), (n) -> Math.tan(n.doubleValue())), true);
	}
}
