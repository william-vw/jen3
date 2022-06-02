package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Exponentiation;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathExponentiation extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathExponentiation() {
        super("http://www.w3.org/2000/10/swap/math#exponentiation", new Exponentiation(), false, true, true);
        setInputConstraints(new InputTwoNumberListAndNumberOrVariable(this));
    }
}
