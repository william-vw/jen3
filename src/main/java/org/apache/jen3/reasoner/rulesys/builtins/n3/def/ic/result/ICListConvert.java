package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_Collection;

public class ICListConvert extends ICCompositeConvert {

	private static final long serialVersionUID = 6143641652408962175L;

	public ICListConvert() {
		super(NodeFactory.createCollection());
	}

	public ICListConvert(Node n) {
		super(n);
	}

	@Override
	public void add(ICConvert i) {
		((Node_Collection) node).addElement(i.getNode());
	}
}
