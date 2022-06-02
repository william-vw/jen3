package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Prefix;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogPrefix extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogPrefix() {
        super("http://www.w3.org/2000/10/swap/log#prefix", new Prefix(), false, true, true);
        setInputConstraints(new InputIriOrVariableAndStringOrVariable(this));
    }
}
