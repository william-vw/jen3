package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Sort;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListSort extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListSort() {
        super("http://www.w3.org/2000/10/swap/list#sort", new Sort(), false, true, true);
        setInputConstraints(new InputListSort(this));
    }
}
