package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Conjunction extends LogBuiltin {

	public Conjunction() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection list = (Node_Collection) n;

			if (list.size() == 0)
				return NodeFactory.createCitedFormula(Scope.outer());

			Node_CitedFormula ret = null;
			N3Model model = null;

			for (Node element : list.getElements()) {
				Node_CitedFormula cf = (Node_CitedFormula) element;

				if (ret == null) {
					ret = NodeFactory.createCitedFormula(cf.getScope());
					model = ret.getContents();
				}

				model.add(cf.getContents());
			}
			
			ret.close();
			
			return ret;

		}, null), true);
	}
}
