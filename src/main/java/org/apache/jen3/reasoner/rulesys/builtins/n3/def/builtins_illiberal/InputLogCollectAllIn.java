package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.FormulaIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.BaseIriIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputLogCollectAllIn extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputLogCollectAllIn(BuiltinDefinition builtin) {
        super(builtin, null);
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new ListIC(null, null, new VariableIC(), new OneOfIC(new FormulaIC(), new VariableIC()), new OneOfIC(new ListIC(null, null), new VariableIC())), new VariableIC()), new OneOfIC(new BaseIriIC(), new FormulaIC(), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new ListIC(null, null, new VariableIC(), new FormulaIC(), new OneOfIC(new ListIC(null, null), new VariableIC())), new OneOfIC(new BaseIriIC(), new FormulaIC(), new VariableIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
