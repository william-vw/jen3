package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Substring;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringSubstring extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringSubstring() {
        super("http://www.w3.org/2000/10/swap/string#substring", new Substring(), false, true, true);
        setInputConstraints(new InputStringSubstring(this));
    }
}
