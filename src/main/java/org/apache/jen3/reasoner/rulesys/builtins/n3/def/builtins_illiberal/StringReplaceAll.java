package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.ReplaceAll;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringReplaceAll extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringReplaceAll() {
        super("http://www.w3.org/2000/10/swap/string#replaceAll", new ReplaceAll(), false, true, true);
        setInputConstraints(new InputStringReplaceAll(this));
    }
}
