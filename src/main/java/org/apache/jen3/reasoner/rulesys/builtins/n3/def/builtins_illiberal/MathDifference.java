package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Difference;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathDifference extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathDifference() {
        super("http://www.w3.org/2000/10/swap/math#difference", new Difference(), false, true, true);
        setInputConstraints(new InputTwoNumberListAndNumberOrVariable(this));
    }
}
