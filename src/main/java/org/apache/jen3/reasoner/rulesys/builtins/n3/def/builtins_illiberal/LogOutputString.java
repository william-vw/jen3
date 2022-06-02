package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogOutputString extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogOutputString() {
        super("http://www.w3.org/2000/10/swap/log#outputString", null, false, false, false);
        setInputConstraints(new InputStringAndString(this));
    }
}
