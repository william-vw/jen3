package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.StringableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.DatatypeIC;
import org.apache.jen3.datatypes.TypeMapper;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputLogHasPrefix extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputLogHasPrefix(BuiltinDefinition builtin) {
        super(builtin, null);
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new OneOfIC(new StringableIC(), new VariableIC()), new OneOfIC(new DatatypeIC(TypeMapper.getInstance().getSafeTypeByName("http://www.w3.org/2001/XMLSchema#boolean"), true), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new StringableIC(), new OneOfIC(new DatatypeIC(TypeMapper.getInstance().getSafeTypeByName("http://www.w3.org/2001/XMLSchema#boolean"), true), new VariableIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
