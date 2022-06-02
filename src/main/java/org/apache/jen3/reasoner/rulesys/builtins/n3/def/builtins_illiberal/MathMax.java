package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Max;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathMax extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathMax() {
        super("http://www.w3.org/2000/10/swap/math#max", new Max(), false, true, true);
        setInputConstraints(new InputConcreteNumberListAndNumberOrVariable(this));
    }
}
