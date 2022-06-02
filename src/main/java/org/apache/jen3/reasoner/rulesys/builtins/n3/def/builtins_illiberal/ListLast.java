package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Last;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListLast extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListLast() {
        super("http://www.w3.org/2000/10/swap/list#last", new Last(), false, true, true);
        setInputConstraints(new InputListAndAny(this));
    }
}
