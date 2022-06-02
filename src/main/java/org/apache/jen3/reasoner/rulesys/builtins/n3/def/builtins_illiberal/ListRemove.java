package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Remove;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListRemove extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListRemove() {
        super("http://www.w3.org/2000/10/swap/list#remove", new Remove(), false, true, true);
        setInputConstraints(new InputListRemove(this));
    }
}
