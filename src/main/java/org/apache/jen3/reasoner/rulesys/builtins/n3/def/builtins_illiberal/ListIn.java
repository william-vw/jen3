package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.In;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListIn extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListIn() {
        super("http://www.w3.org/2000/10/swap/list#in", new In(), false, true, true);
        setInputConstraints(new InputAnyAndList(this));
    }
}
