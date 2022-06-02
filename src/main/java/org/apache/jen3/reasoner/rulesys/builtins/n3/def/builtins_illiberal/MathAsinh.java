package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Asinh;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathAsinh extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathAsinh() {
        super("http://www.w3.org/2000/10/swap/math#asinh", new Asinh(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
