package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.AbsoluteValue;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathAbsoluteValue extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathAbsoluteValue() {
        super("http://www.w3.org/2000/10/swap/math#absoluteValue", new AbsoluteValue(), false, true, true);
        setInputConstraints(new InputNumberAndNumberOrVariable(this));
    }
}
