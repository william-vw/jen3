package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

/**
 * 
 * https://www.w3.org/2000/10/swap/math#
 * https://www.w3.org/2000/10/swap/doc/CwmBuiltins
 * 
 * @author wvw
 *
 */

public abstract class MathBuiltin extends N3Builtin {

	public MathBuiltin() {
		super();
	}

	public MathBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public MathBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
