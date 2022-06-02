package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Replace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringReplace extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringReplace() {
        super("http://www.w3.org/2000/10/swap/string#replace", new Replace(), false, true, true);
        setInputConstraints(new InputStringReplace(this));
    }
}
