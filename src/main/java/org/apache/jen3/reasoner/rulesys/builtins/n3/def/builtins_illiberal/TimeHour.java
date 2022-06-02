package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.Hour;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeHour extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeHour() {
        super("http://www.w3.org/2000/10/swap/time#hour", new Hour(), false, true, true);
        setInputConstraints(new InputTimeAndIntableOrVariable(this));
    }
}
