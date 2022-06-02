package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Cos;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathCos extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathCos() {
        super("http://www.w3.org/2000/10/swap/math#cos", new Cos(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
