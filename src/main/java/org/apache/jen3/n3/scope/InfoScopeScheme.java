package org.apache.jen3.n3.scope;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.RDFObject;

public abstract class InfoScopeScheme extends N3ScopeScheme {

	public InfoScopeScheme(N3Model model) {
		super(model);
	}

	protected String iriPriorVarMsg(Node iri, RDFObject var) {
		return "IRI \"" + iri + "\" has the same identifier as prior variable \"" + var.asQuantifiedVariable().getName()
				+ "\". This is considered bad practice and should be avoided.";
	}

	protected String varPriorIriMsg(Node var, RDFObject iri) {
		return "variable \"" + ((Node_Variable) var).getName() + "\" has the same identifier as prior IRI \"" + iri
				+ "\". This is considered bad practice and should be avoided.";
	}

	protected String varPriorVarMsg(Node newVarNode, RDFObject priorVarObj) {
		Node_QuantifiedVariable newVar = (Node_QuantifiedVariable) newVarNode;
		Node_QuantifiedVariable priorVar = (Node_QuantifiedVariable) priorVarObj.asNode();

		if (newVar.getQuantifier().getQuantifierType() != priorVar.getQuantifier().getQuantifierType())
			return "variable \"" + newVar.getName()
					+ "\" has the same name but a different quantifier as a prior variable. "
					+ "This is considered bad practice and should be avoided.";
		else
			return null;
	}
}
