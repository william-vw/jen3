package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Acos;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathAcos extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathAcos() {
        super("http://www.w3.org/2000/10/swap/math#acos", new Acos(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
