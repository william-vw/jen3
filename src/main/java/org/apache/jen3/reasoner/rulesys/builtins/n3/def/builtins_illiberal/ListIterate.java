package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Iterate;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListIterate extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListIterate() {
        super("http://www.w3.org/2000/10/swap/list#iterate", new Iterate(), false, true, true);
        setInputConstraints(new InputListIterate(this));
    }
}
