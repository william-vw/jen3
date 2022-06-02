package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import static org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinClauses.*;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.AnyIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ConcreteIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;

public class InputConcreteAndConcrete extends InputConstraintsDefinition {

    private static final long serialVersionUID = 1;

    public InputConcreteAndConcrete(BuiltinDefinition builtin) {
        super(builtin, "http://www.w3.org/2000/10/swap/builtin#concreteAndConcrete");
        addClause(ACCEPT, new SubjectObjectStatementIC(this, new AnyIC(), new AnyIC(), null));
        addClause(DOMAIN, new SubjectObjectStatementIC(this, new ConcreteIC(), new ConcreteIC(), null));
        addClause(NOT_BOUND, new BoolStatementIC(this, false));
    }
}
