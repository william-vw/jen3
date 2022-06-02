package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.TimeZone;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeTimeZone extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeTimeZone() {
        super("http://www.w3.org/2000/10/swap/time#timeZone", new TimeZone(), false, true, true);
        setInputConstraints(new InputDateOrTimeAndIntableOrVariable(this));
    }
}
