package org.apache.jen3.reasoner.rulesys.builtins.n3.graph;

import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class GraphBuiltin extends N3Builtin {

	public GraphBuiltin() {
		super();
	}

	public GraphBuiltin(BinaryFlowPattern flowPattern) {
		super(flowPattern);
	}

	public GraphBuiltin(BinaryFlowPattern flowPattern, boolean flowPatternSingleton) {
		super(flowPattern, flowPatternSingleton);
	}
}
