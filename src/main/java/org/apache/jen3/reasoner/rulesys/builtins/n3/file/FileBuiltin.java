package org.apache.jen3.reasoner.rulesys.builtins.n3.file;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class FileBuiltin extends N3Builtin {

	public FileBuiltin() {
		super();
	}

	public FileBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public FileBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
