package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.RemoveAt;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListRemoveAt extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListRemoveAt() {
        super("http://www.w3.org/2000/10/swap/list#removeAt", new RemoveAt(), false, true, true);
        setInputConstraints(new InputListRemoveAt(this));
    }
}
