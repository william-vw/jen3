package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.UpperCase;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringUpperCase extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringUpperCase() {
        super("http://www.w3.org/2000/10/swap/string#upperCase", new UpperCase(), false, true, true);
        setInputConstraints(new InputStringAndStringOrVariable(this));
    }
}
