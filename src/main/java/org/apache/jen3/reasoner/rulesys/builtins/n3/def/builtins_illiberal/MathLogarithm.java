package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Logarithm;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathLogarithm extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathLogarithm() {
        super("http://www.w3.org/2000/10/swap/math#logarithm", new Logarithm(), false, true, true);
        setInputConstraints(new InputTwoNumberListAndNumberOrVariable(this));
    }
}
