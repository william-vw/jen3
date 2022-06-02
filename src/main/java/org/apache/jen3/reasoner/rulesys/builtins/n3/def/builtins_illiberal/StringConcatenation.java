package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Concatenation;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringConcatenation extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringConcatenation() {
        super("http://www.w3.org/2000/10/swap/string#concatenation", new Concatenation(), false, true, true);
        setInputConstraints(new InputConcreteStringListAndStringOrVariable(this));
    }
}
