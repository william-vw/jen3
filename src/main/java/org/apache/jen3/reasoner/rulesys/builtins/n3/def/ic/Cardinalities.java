package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.vocabulary.BuiltinNS;

public enum Cardinalities {
	EXACT(BuiltinNS.Exact), MIN(BuiltinNS.Min), MAX(BuiltinNS.Max);

	private Resource uri;

	private Cardinalities(Resource uri) {
		this.uri = uri;
	}

	public Resource getUri() {
		return uri;
	}
}
