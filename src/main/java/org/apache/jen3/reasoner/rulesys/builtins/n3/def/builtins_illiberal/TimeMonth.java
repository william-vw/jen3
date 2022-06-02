package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.Month;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeMonth extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeMonth() {
        super("http://www.w3.org/2000/10/swap/time#month", new Month(), false, true, true);
        setInputConstraints(new InputDateAndIntableOrVariable(this));
    }
}
