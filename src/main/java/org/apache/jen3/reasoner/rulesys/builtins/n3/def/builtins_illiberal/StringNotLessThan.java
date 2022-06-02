package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.NotLessThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringNotLessThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringNotLessThan() {
        super("http://www.w3.org/2000/10/swap/string#notLessThan", new NotLessThan(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
