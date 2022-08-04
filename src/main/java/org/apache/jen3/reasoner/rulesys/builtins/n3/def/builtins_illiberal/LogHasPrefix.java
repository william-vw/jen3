package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.HasPrefix;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogHasPrefix extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogHasPrefix() {
        super("http://www.w3.org/2000/10/swap/log#hasPrefix", new HasPrefix(), false, true, true);
        setInputConstraints(new InputLogHasPrefix(this));
    }
}
