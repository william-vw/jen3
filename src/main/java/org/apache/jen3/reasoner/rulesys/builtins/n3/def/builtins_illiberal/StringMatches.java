package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Matches;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringMatches extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringMatches() {
        super("http://www.w3.org/2000/10/swap/string#matches", new Matches(), false, true, true);
        setInputConstraints(new InputStringAndRegex(this));
    }
}
