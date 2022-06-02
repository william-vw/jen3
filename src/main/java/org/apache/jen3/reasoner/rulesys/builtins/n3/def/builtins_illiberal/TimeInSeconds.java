package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.InSeconds;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeInSeconds extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeInSeconds() {
        super("http://www.w3.org/2000/10/swap/time#inSeconds", new InSeconds(), false, true, true);
        setInputConstraints(new InputTimeInSeconds(this));
    }
}
