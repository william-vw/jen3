package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Sinh;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathSinh extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathSinh() {
        super("http://www.w3.org/2000/10/swap/math#sinh", new Sinh(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
