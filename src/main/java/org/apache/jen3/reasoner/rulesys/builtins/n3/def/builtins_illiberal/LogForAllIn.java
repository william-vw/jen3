package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.ForAllIn;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogForAllIn extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogForAllIn() {
        super("http://www.w3.org/2000/10/swap/log#forAllIn", new ForAllIn(), false, true, true);
        setInputConstraints(new InputLogForAllIn(this));
    }
}
