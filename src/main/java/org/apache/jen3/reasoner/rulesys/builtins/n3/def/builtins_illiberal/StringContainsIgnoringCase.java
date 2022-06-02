package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.ContainsIgnoringCase;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringContainsIgnoringCase extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringContainsIgnoringCase() {
        super("http://www.w3.org/2000/10/swap/string#containsIgnoringCase", new ContainsIgnoringCase(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
