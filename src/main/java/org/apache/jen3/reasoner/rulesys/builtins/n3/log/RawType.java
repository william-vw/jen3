package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;
import org.apache.jen3.vocabulary.N3Log;

public class RawType extends LogBuiltin {

	public RawType() {		
		super(new BinaryFlowPattern((n, g) -> {
			switch (n.getType()) {
			
			case URI:
				return N3Log.Uri.asNode();
				
			case BLANK:
				return N3Log.BlankNode.asNode();
			
			case CITED_FORMULA:
				return N3Log.Formula.asNode();
			
			case LITERAL:
				return N3Log.Literal.asNode();
				
			case COLLECTION:
				return N3Log.List.asNode();
				
			default:
				return N3Log.Other.asNode();
			}
			
		}, null), true);
	}
}
