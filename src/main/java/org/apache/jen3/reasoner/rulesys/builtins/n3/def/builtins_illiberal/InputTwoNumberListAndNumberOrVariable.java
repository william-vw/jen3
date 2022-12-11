package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinality;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.NumberableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinalities.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputTwoNumberListAndNumberOrVariable extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputTwoNumberListAndNumberOrVariable(BuiltinDefinition builtin) {
        super(builtin, "http://www.w3.org/2000/10/swap/builtin#twoNumberListAndNumberOrVariable");
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new ListIC(new Cardinality(EXACT, 2), new OneOfIC(new NumberableIC(), new VariableIC())), new VariableIC()), new OneOfIC(new NumberableIC(), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new ListIC(new Cardinality(EXACT, 2), new NumberableIC()), new OneOfIC(new NumberableIC(), new VariableIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
