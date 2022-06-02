package org.apache.jen3.graph.n3;

import org.apache.jen3.graph.BaseNodeVisitor;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.reasoner.rulesys.Util;

/**
 * Visitor that allows modifying cited formulas. Since those are immutable, this
 * class will make a copy of an encountered cited formula and call the
 * {@link ModifyFormulaNodeVisitor#visitFormulaContent(Triple)} method for each
 * of the triples that will be added to the copy.
 * 
 * Subclasses will need to initialize the "current" variable to point to the
 * initial scope.
 * 
 * 
 * @author wvw
 *
 */

public abstract class ModifyFormulaNodeVisitor extends BaseNodeVisitor {

	protected Scope current;
	protected int level = 0;

	protected ModifyFormulaNodeVisitor() {
	}

	protected ModifyFormulaNodeVisitor(Scope current, int level) {
		this.current = current;
		this.level = level;
	}

	@Override
	public Node visitCitedFormula(Node_CitedFormula it, Object data) {
		Graph g = it.getContents().getGraph();

		level++;

		Scope outer = current;
		current = new Scope(Scopes.GRAPH);

		Node_CitedFormula cf2 = NodeFactory.createCitedFormula(it.getScope());
		N3Model m2 = cf2.open();
		Util.baseOn(m2, it.getContents());

		Graph g2 = m2.getGraph();
		g.find().forEachRemaining(t -> g2.add(visitFormulaContent(t, data)));

		current = outer;
		level--;

		if (current != null)
			current.sub(Scopes.GRAPH).attach(cf2);

		if (level == 0)
			current = null;

		return cf2;
	}

	protected abstract Triple visitFormulaContent(Triple t, Object data);
}