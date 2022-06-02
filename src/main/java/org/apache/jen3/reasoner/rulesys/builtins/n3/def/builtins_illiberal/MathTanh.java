package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Tanh;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathTanh extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathTanh() {
        super("http://www.w3.org/2000/10/swap/math#tanh", new Tanh(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
