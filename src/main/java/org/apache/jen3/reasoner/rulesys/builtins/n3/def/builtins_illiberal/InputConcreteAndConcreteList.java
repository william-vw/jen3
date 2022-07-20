package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.AnyIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.VariableIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ConcreteIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputConcreteAndConcreteList extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputConcreteAndConcreteList(BuiltinDefinition builtin) {
        super(builtin, "http://www.w3.org/2000/10/swap/builtin#concreteAndConcreteList");
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new AnyIC(), new OneOfIC(new ListIC(null, null), new VariableIC()), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new ConcreteIC(), new ListIC(null, new ConcreteIC()), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
