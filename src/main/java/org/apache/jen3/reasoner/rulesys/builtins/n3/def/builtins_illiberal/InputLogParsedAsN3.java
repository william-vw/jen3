package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.StringIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.FormulaIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.OneOfStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputLogParsedAsN3 extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputLogParsedAsN3(BuiltinDefinition builtin) {
        super(builtin, null);
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new StringIC(), new VariableIC()), new OneOfIC(new FormulaIC(), new VariableIC()), null));
        addClause(DOMAIN, new OneOfStatementIC(this, new SubjectObjectStatementIC(this, new StringIC(), new OneOfIC(new FormulaIC(), new VariableIC()), null), new SubjectObjectStatementIC(this, new VariableIC(), new FormulaIC(), null)));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
