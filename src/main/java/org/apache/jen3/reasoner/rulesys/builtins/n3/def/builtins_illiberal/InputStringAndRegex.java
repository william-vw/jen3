package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.StringableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.RegexIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputStringAndRegex extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputStringAndRegex(BuiltinDefinition builtin) {
        super(builtin, "http://www.w3.org/2000/10/swap/builtin#stringAndRegex");
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new StringableIC(), new VariableIC()), new OneOfIC(new RegexIC(), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new StringableIC(), new RegexIC(), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
