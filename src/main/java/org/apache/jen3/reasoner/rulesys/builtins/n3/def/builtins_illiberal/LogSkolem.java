package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Skolem;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogSkolem extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogSkolem() {
        super("http://www.w3.org/2000/10/swap/log#skolem", new Skolem(), false, true, true);
        setInputConstraints(new InputLogSkolem(this));
    }
}
