package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.NotMember;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListNotMember extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListNotMember() {
        super("http://www.w3.org/2000/10/swap/list#notMember", new NotMember(), false, true, true);
        setInputConstraints(new InputConcreteListAndConcrete(this));
    }
}
