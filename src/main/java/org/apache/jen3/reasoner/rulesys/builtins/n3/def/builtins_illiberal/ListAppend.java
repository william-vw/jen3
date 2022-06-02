package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Append;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListAppend extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListAppend() {
        super("http://www.w3.org/2000/10/swap/list#append", new Append(), false, true, true);
        setInputConstraints(new InputListAppend(this));
    }
}
