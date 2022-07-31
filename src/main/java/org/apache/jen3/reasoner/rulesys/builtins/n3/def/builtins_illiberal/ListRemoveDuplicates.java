package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.RemoveDuplicates;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListRemoveDuplicates extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListRemoveDuplicates() {
        super("http://www.w3.org/2000/10/swap/list#removeDuplicates", new RemoveDuplicates(), false, true, true);
        setInputConstraints(new InputListRemoveDuplicates(this));
    }
}
