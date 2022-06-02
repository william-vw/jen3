package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.NumberableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputNumberAndNumberOrVariable extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputNumberAndNumberOrVariable(BuiltinDefinition builtin) {
        super(builtin, "http://www.w3.org/2000/10/swap/builtin#numberAndNumberOrVariable");
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new NumberableIC(), new VariableIC()), new OneOfIC(new NumberableIC(), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new NumberableIC(), new OneOfIC(new NumberableIC(), new VariableIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
