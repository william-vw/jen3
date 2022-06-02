package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Ceiling;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathCeiling extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathCeiling() {
        super("http://www.w3.org/2000/10/swap/math#ceiling", new Ceiling(), false, true, true);
        setInputConstraints(new InputNumberAndNumberOrVariable(this));
    }
}
