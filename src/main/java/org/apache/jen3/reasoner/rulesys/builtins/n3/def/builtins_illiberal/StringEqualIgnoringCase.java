package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.EqualIgnoringCase;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringEqualIgnoringCase extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringEqualIgnoringCase() {
        super("http://www.w3.org/2000/10/swap/string#equalIgnoringCase", new EqualIgnoringCase(), false, true, true);
        setInputConstraints(new InputStringAndString(this));
    }
}
