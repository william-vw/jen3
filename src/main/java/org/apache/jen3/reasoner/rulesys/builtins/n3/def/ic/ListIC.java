package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.util.Arrays;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICCompositeConvert;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICListConvert;

public class ListIC extends CompositeIC {

	private static final long serialVersionUID = -549507542964155517L;

	public ListIC() {
		super(DefaultICs.LIST);
	}

	public ListIC(Cardinality size, InputConstraint elementType, InputConstraint... elements) {
		super(DefaultICs.LIST, size, elementType, Arrays.asList(elements));
	}

	@Override
	protected boolean checkCompositeType(Node n) {
		return n.isCollection();
	}

	@Override
	protected ICCompositeConvert getCompositeConvert() {
		return new ICListConvert();
	}

	@Override
	protected ICCompositeConvert getCompositeConvert(Node n) {
		return new ICListConvert(n);
	}

	@Override
	protected List<Node> getCompositeElements(Node n) {
		return ((Node_Collection) n).getElements();
	}
}