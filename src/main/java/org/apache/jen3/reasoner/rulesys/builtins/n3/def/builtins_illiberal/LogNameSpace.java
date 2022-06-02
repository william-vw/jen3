package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.NameSpace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogNameSpace extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogNameSpace() {
        super("http://www.w3.org/2000/10/swap/log#nameSpace", new NameSpace(), false, true, true);
        setInputConstraints(new InputIriAndStringOrVariable(this));
    }
}
