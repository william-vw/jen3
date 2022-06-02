package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.NotEqualIgnoringCase;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringNotEqualIgnoringCase extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringNotEqualIgnoringCase() {
        super("http://www.w3.org/2000/10/swap/string#notEqualIgnoringCase", new NotEqualIgnoringCase(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
