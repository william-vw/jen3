package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.NotGreaterThan;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringNotGreaterThan extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringNotGreaterThan() {
        super("http://www.w3.org/2000/10/swap/string#notGreaterThan", new NotGreaterThan(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
