package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.TypeMapper;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;

public class Dtlit extends ToLit {

	@Override
	protected Node litFromExtra(Node string, Node extra) {
		String datatypeURI = extra.getURI();
		RDFDatatype dType = TypeMapper.getInstance().getSafeTypeByName(datatypeURI);
		
		return NodeFactory.createLiteralByValue(string.getLiteralValue(), dType);
	}

	@Override
	protected Node extraFromLit(Node lit) {
		return NodeFactory.createURI(lit.getLiteralDatatypeURI());
	}
}
