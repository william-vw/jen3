package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.vocabulary.BuiltinNS;

public enum BuiltinClauses {
	ACCEPT(BuiltinNS.accept), DOMAIN(BuiltinNS.domain), NOT_BOUND(BuiltinNS.notBound);

	private Resource prp;

	private BuiltinClauses(Resource prp) {
		this.prp = prp;
	}

	public Resource getPrp() {
		return prp;
	}
}
