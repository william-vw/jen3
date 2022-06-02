package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Tan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathTan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathTan() {
        super("http://www.w3.org/2000/10/swap/math#tan", new Tan(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
