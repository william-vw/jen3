package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Uuid;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogUuid extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogUuid() {
        super("http://www.w3.org/2000/10/swap/log#uuid", new Uuid(), false, true, true);
        setInputConstraints(new InputLogUuid(this));
    }
}
