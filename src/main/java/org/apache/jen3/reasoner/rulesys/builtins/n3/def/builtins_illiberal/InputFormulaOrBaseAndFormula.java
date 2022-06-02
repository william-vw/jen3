package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.FormulaIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.BaseIriIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputFormulaOrBaseAndFormula extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputFormulaOrBaseAndFormula(BuiltinDefinition builtin) {
        super(builtin, "http://www.w3.org/2000/10/swap/builtin#formulaOrBaseAndFormula");
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new FormulaIC(), new VariableIC(), new BaseIriIC()), new OneOfIC(new FormulaIC(), new VariableIC(), new BaseIriIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new OneOfIC(new FormulaIC(), new BaseIriIC()), new OneOfIC(new FormulaIC(), new VariableIC(), new BaseIriIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
