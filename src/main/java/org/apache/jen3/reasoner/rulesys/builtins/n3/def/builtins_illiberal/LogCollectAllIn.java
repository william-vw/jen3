package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.CollectAllIn;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogCollectAllIn extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogCollectAllIn() {
        super("http://www.w3.org/2000/10/swap/log#collectAllIn", new CollectAllIn(), false, true, true);
        setInputConstraints(new InputLogCollectAllIn(this));
    }
}
