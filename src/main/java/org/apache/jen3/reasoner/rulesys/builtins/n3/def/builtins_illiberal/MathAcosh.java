package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Acosh;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathAcosh extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathAcosh() {
        super("http://www.w3.org/2000/10/swap/math#acosh", new Acosh(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
