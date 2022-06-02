package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.Member;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListMember extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListMember() {
        super("http://www.w3.org/2000/10/swap/list#member", new Member(), false, true, true);
        setInputConstraints(new InputListAndAny(this));
    }
}
