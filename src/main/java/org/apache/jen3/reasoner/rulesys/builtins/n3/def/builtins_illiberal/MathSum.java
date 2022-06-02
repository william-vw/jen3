package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Sum;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathSum extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathSum() {
        super("http://www.w3.org/2000/10/swap/math#sum", new Sum(), false, true, true);
        setInputConstraints(new InputConcreteNumberListAndNumberOrVariable(this));
    }
}
