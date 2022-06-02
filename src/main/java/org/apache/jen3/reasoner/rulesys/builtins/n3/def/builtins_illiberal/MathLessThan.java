package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.LessThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathLessThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathLessThan() {
        super("http://www.w3.org/2000/10/swap/math#lessThan", new LessThan(), false, true, true);
        setInputConstraints(new InputConcreteNumbers(this));
    }
}
