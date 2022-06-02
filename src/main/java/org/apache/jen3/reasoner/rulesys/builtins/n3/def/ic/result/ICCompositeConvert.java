package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result;

import org.apache.jen3.graph.Node;

public abstract class ICCompositeConvert extends ICConvert {

	private static final long serialVersionUID = -6494971134584599888L;

	public ICCompositeConvert(Node n) {
		super(n);
	}

	public abstract void add(ICConvert i);
}
