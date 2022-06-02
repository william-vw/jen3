package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Floor;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathFloor extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathFloor() {
        super("http://www.w3.org/2000/10/swap/math#floor", new Floor(), false, true, true);
        setInputConstraints(new InputNumberAndNumberOrVariable(this));
    }
}
