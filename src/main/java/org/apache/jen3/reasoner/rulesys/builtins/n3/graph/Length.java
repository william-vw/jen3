package org.apache.jen3.reasoner.rulesys.builtins.n3.graph;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Length extends GraphBuiltin {

	public Length() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_CitedFormula cf = (Node_CitedFormula) n;
			return NodeFactory.createLiteralByValue(cf.getContents().size(), XSDDatatype.XSDlong);

		}, null), true);
	}
}
