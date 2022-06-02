package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Unique;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListUnique extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListUnique() {
        super("http://www.w3.org/2000/10/swap/list#unique", new Unique(), false, true, true);
        setInputConstraints(new InputListUnique(this));
    }
}
