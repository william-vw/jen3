package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.RawType;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogRawType extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogRawType() {
        super("http://www.w3.org/2000/10/swap/log#rawType", new RawType(), false, true, true);
        setInputConstraints(new InputLogRawType(this));
    }
}
