package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Atanh;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathAtanh extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathAtanh() {
        super("http://www.w3.org/2000/10/swap/math#atanh", new Atanh(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
