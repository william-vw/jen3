package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.StringableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.RegexIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputStringScrape extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputStringScrape(BuiltinDefinition builtin) {
        super(builtin, null);
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new ListIC(null, null, new OneOfIC(new StringableIC(), new VariableIC()), new OneOfIC(new RegexIC(), new VariableIC())), new VariableIC()), new OneOfIC(new StringableIC(), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new ListIC(null, null, new StringableIC(), new RegexIC()), new OneOfIC(new StringableIC(), new VariableIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
