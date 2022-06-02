package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Bound;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogBound extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogBound() {
        super("http://www.w3.org/2000/10/swap/log#bound", new Bound(), false, true, true);
        setInputConstraints(new InputLogBound(this));
    }
}
