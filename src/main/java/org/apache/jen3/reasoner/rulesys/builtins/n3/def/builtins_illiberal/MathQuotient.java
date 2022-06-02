package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Quotient;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathQuotient extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathQuotient() {
        super("http://www.w3.org/2000/10/swap/math#quotient", new Quotient(), false, true, true);
        setInputConstraints(new InputTwoNumberListAndNumberOrVariable(this));
    }
}
