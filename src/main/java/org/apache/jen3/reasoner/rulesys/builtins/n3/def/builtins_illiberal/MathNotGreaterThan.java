package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.NotGreaterThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathNotGreaterThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathNotGreaterThan() {
        super("http://www.w3.org/2000/10/swap/math#notGreaterThan", new NotGreaterThan(), false, true, true);
        setInputConstraints(new InputConcreteNumbers(this));
    }
}
