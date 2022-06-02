package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.RoundedTo;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathRoundedTo extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathRoundedTo() {
        super("http://www.w3.org/2000/10/swap/math#roundedTo", new RoundedTo(), false, true, true);
        setInputConstraints(new InputNumberIntListAndNumberOrVariable(this));
    }
}
