package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.Year;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeYear extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeYear() {
        super("http://www.w3.org/2000/10/swap/time#year", new Year(), false, true, true);
        setInputConstraints(new InputDateAndIntableOrVariable(this));
    }
}
