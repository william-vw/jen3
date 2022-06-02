package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Min;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathMin extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathMin() {
        super("http://www.w3.org/2000/10/swap/math#min", new Min(), false, true, true);
        setInputConstraints(new InputConcreteNumberListAndNumberOrVariable(this));
    }
}
