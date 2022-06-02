package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Sin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathSin extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathSin() {
        super("http://www.w3.org/2000/10/swap/math#sin", new Sin(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
