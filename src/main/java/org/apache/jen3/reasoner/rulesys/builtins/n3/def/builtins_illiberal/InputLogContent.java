package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.IriIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.StringableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputLogContent extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputLogContent(BuiltinDefinition builtin) {
        super(builtin, null);
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new IriIC(), new VariableIC()), new OneOfIC(new StringableIC(), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new IriIC(), new OneOfIC(new StringableIC(), new VariableIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
