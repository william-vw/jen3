package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.LessThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringLessThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringLessThan() {
        super("http://www.w3.org/2000/10/swap/string#lessThan", new LessThan(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
