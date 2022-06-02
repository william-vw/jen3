package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.First;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListFirst extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListFirst() {
        super("http://www.w3.org/2000/10/swap/list#first", new First(), false, true, true);
        setInputConstraints(new InputListAndAny(this));
    }
}
