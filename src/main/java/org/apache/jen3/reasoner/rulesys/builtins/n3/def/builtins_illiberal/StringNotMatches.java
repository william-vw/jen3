package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.NotMatches;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringNotMatches extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringNotMatches() {
        super("http://www.w3.org/2000/10/swap/string#notMatches", new NotMatches(), false, true, true);
        setInputConstraints(new InputStringAndRegex(this));
    }
}
