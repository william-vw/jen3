package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.list.MemberAt;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class ListMemberAt extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public ListMemberAt() {
        super("http://www.w3.org/2000/10/swap/list#memberAt", new MemberAt(), false, true, true);
        setInputConstraints(new InputListMemberAt(this));
    }
}
