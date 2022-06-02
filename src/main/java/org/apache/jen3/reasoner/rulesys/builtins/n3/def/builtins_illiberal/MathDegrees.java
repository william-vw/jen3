package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Degrees;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathDegrees extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathDegrees() {
        super("http://www.w3.org/2000/10/swap/math#degrees", new Degrees(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
