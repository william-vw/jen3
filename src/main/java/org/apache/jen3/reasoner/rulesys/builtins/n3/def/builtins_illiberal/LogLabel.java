package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Label;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogLabel extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogLabel() {
        super("http://www.w3.org/2000/10/swap/log#label", new Label(), false, true, true);
        setInputConstraints(new InputBnodeAndStringOrVariable(this));
    }
}
