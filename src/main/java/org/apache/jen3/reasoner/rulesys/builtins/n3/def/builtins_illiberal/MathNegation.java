package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Negation;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathNegation extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathNegation() {
        super("http://www.w3.org/2000/10/swap/math#negation", new Negation(), false, true, true);
        setInputConstraints(new InputNumberOrVariable(this));
    }
}
