package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.EndsWith;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringEndsWith extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringEndsWith() {
        super("http://www.w3.org/2000/10/swap/string#endsWith", new EndsWith(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
