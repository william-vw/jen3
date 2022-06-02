package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Cosh;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathCosh extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathCosh() {
        super("http://www.w3.org/2000/10/swap/math#cosh", new Cosh(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
