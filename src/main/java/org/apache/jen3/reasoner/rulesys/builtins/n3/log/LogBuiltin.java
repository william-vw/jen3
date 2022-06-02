package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.FlowPattern;

/**
 * 
 * https://www.w3.org/2000/10/swap/log#
 * https://www.w3.org/2000/10/swap/doc/CwmBuiltins
 * 
 * @author wvw
 *
 */
public abstract class LogBuiltin extends N3Builtin {

	public LogBuiltin() {
		super();
	}

	public LogBuiltin(FlowPattern flowPattern) {
		super(flowPattern);
	}

	public LogBuiltin(FlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
