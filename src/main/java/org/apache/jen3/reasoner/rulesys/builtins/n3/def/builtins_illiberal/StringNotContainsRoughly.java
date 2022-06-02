package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.NotContainsRoughly;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringNotContainsRoughly extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringNotContainsRoughly() {
        super("http://www.w3.org/2000/10/swap/string#notContainsRoughly", new NotContainsRoughly(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
