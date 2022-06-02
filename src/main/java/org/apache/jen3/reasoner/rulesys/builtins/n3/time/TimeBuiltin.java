package org.apache.jen3.reasoner.rulesys.builtins.n3.time;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public abstract class TimeBuiltin extends N3Builtin {

	public static final String BASE_URI = "http://www.w3.org/2000/10/swap/time#";

	public TimeBuiltin() {
		super();
	}

	public TimeBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public TimeBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
