package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.LowerCase;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringLowerCase extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringLowerCase() {
        super("http://www.w3.org/2000/10/swap/string#lowerCase", new LowerCase(), false, true, true);
        setInputConstraints(new InputStringAndStringOrVariable(this));
    }
}
