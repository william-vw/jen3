package org.apache.jen3.reasoner.rulesys.builtins.n3.time;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.datatypes.xsd.XSDDateTime;

public class Second extends TimeCompare {

	@Override
	public Pair<Object, XSDDatatype> getCmp(XSDDateTime t) {
		return new ImmutablePair<>(t.getFullSeconds(), XSDDatatype.XSDint);
	}
}
