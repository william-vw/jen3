package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.NotEqualTo;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogNotEqualTo extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogNotEqualTo() {
        super("http://www.w3.org/2000/10/swap/log#notEqualTo", new NotEqualTo(), false, true, true);
        setInputConstraints(new InputConcreteAndConcrete(this));
    }
}
