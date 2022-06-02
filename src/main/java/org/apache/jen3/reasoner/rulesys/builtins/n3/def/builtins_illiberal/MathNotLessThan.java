package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.NotLessThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathNotLessThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathNotLessThan() {
        super("http://www.w3.org/2000/10/swap/math#notLessThan", new NotLessThan(), false, true, true);
        setInputConstraints(new InputConcreteNumbers(this));
    }
}
