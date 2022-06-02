package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.Day;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeDay extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeDay() {
        super("http://www.w3.org/2000/10/swap/time#day", new Day(), false, true, true);
        setInputConstraints(new InputDateAndIntableOrVariable(this));
    }
}
