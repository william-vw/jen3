package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Length;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListLength extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListLength() {
        super("http://www.w3.org/2000/10/swap/list#length", new Length(), false, true, true);
        setInputConstraints(new InputListLength(this));
    }
}
