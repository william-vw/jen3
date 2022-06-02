package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Join;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringJoin extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringJoin() {
        super("http://www.w3.org/2000/10/swap/string#join", new Join(), false, true, true);
        setInputConstraints(new InputStringJoin(this));
    }
}
