package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.NotIn;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListNotIn extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListNotIn() {
        super("http://www.w3.org/2000/10/swap/list#notIn", new NotIn(), false, true, true);
        setInputConstraints(new InputConcreteAndConcreteList(this));
    }
}
