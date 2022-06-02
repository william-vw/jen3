package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Format;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringFormat extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringFormat() {
        super("http://www.w3.org/2000/10/swap/string#format", new Format(), false, true, true);
        setInputConstraints(new InputConcreteStringListAndStringOrVariable(this));
    }
}
