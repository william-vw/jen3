package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Atan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathAtan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathAtan() {
        super("http://www.w3.org/2000/10/swap/math#atan", new Atan(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
