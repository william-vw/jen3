package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.EqualTo;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathEqualTo extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathEqualTo() {
        super("http://www.w3.org/2000/10/swap/math#equalTo", new EqualTo(), false, true, true);
        setInputConstraints(new InputConcreteNumbers(this));
    }
}
