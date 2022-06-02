package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Rounded;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathRounded extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathRounded() {
        super("http://www.w3.org/2000/10/swap/math#rounded", new Rounded(), false, true, true);
        setInputConstraints(new InputNumberAndNumberOrVariable(this));
    }
}
