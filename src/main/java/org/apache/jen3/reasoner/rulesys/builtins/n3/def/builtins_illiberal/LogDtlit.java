package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Dtlit;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogDtlit extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogDtlit() {
        super("http://www.w3.org/2000/10/swap/log#dtlit", new Dtlit(), false, true, true);
        setInputConstraints(new InputLogDtlit(this));
    }
}
