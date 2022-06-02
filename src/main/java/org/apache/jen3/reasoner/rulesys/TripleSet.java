package org.apache.jen3.reasoner.rulesys;

import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.iterator.TripleForMatchIterator;

public abstract class TripleSet {

	protected TriplePattern clause;

	public TripleSet(TriplePattern clause) {
		this.clause = clause;
	}

	public TriplePattern getClause() {
		return clause;
	}
	
	public abstract boolean requiresMatching();

	public abstract TripleForMatchIterator triples(BindingStack env);
}
