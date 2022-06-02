package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

/**
 * 
 * https://www.w3.org/2000/10/swap/list#
 * https://www.w3.org/2000/10/swap/doc/CwmBuiltins
 * 
 * @author wvw
 *
 */
public abstract class ListBuiltin extends N3Builtin {

	public static final String BASE_URI = "http://www.w3.org/2000/10/swap/list#";

	public ListBuiltin() {
		super();
	}

	public ListBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public ListBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
