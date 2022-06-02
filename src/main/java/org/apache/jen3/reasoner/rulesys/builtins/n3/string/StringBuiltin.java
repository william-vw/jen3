package org.apache.jen3.reasoner.rulesys.builtins.n3.string;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

/**
 * 
 * https://www.w3.org/2000/10/swap/string#
 * https://www.w3.org/2000/10/swap/doc/CwmBuiltins
 * 
 * @author wvw
 *
 */

public abstract class StringBuiltin extends N3Builtin {

	public StringBuiltin() {
		super();
	}

	public StringBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public StringBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}