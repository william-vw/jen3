package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Contains;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringContains extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringContains() {
        super("http://www.w3.org/2000/10/swap/string#contains", new Contains(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
