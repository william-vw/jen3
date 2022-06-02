package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.Minute;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeMinute extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeMinute() {
        super("http://www.w3.org/2000/10/swap/time#minute", new Minute(), false, true, true);
        setInputConstraints(new InputTimeAndIntableOrVariable(this));
    }
}
