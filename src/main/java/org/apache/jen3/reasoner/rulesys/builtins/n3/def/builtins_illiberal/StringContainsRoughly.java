package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.ContainsRoughly;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringContainsRoughly extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringContainsRoughly() {
        super("http://www.w3.org/2000/10/swap/string#containsRoughly", new ContainsRoughly(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
