package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Uri;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogUri extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogUri() {
        super("http://www.w3.org/2000/10/swap/log#uri", new Uri(), false, true, true);
        setInputConstraints(new InputLogUri(this));
    }
}
