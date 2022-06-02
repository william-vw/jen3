package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.EqualTo;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogEqualTo extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogEqualTo() {
        super("http://www.w3.org/2000/10/swap/log#equalTo", new EqualTo(), false, true, true);
        setInputConstraints(new InputConcreteAndConcreteOrVariable(this));
    }
}
