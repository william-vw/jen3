package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.StartsWith;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringStartsWith extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringStartsWith() {
        super("http://www.w3.org/2000/10/swap/string#startsWith", new StartsWith(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
