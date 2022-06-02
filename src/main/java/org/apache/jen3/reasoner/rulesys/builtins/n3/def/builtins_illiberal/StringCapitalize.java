package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Capitalize;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringCapitalize extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringCapitalize() {
        super("http://www.w3.org/2000/10/swap/string#capitalize", new Capitalize(), false, true, true);
        setInputConstraints(new InputStringAndStringOrVariable(this));
    }
}
