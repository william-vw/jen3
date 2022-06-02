package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.GreaterThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathGreaterThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathGreaterThan() {
        super("http://www.w3.org/2000/10/swap/math#greaterThan", new GreaterThan(), false, true, true);
        setInputConstraints(new InputConcreteNumbers(this));
    }
}
