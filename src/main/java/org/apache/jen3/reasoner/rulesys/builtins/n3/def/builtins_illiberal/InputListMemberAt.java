package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.IntableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.AnyIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputListMemberAt extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputListMemberAt(BuiltinDefinition builtin) {
        super(builtin, null);
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new ListIC(null, null, new OneOfIC(new ListIC(null, null), new VariableIC()), new OneOfIC(new IntableIC(), new VariableIC())), new VariableIC()), new AnyIC(), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new ListIC(null, null, new ListIC(null, null), new IntableIC()), new AnyIC(), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
