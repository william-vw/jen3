package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.GreaterThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringGreaterThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringGreaterThan() {
        super("http://www.w3.org/2000/10/swap/string#greaterThan", new GreaterThan(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
