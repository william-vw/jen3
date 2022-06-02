package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Remainder;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathRemainder extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathRemainder() {
        super("http://www.w3.org/2000/10/swap/math#remainder", new Remainder(), false, true, true);
        setInputConstraints(new InputIntIntListAndIntOrVariable(this));
    }
}
