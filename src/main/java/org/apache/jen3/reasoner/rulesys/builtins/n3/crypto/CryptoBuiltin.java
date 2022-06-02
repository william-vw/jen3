package org.apache.jen3.reasoner.rulesys.builtins.n3.crypto;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public abstract class CryptoBuiltin extends N3Builtin {

	public CryptoBuiltin() {
		super();
	}

	public CryptoBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public CryptoBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
