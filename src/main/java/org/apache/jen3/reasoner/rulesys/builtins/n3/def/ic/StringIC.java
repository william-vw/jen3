package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.datatypes.xsd.XSDDatatype;

public class StringIC extends DatatypeIC {

	private static final long serialVersionUID = -1437382900339058984L;

	public StringIC() {
		super(DefaultICs.STRING, XSDDatatype.XSDstring, false);
	}
}