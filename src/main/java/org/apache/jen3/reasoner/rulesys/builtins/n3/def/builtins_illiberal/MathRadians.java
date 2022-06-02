package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Radians;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathRadians extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathRadians() {
        super("http://www.w3.org/2000/10/swap/math#radians", new Radians(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
