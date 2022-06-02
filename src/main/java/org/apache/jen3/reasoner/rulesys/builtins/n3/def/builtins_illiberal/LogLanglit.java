package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Langlit;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogLanglit extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogLanglit() {
        super("http://www.w3.org/2000/10/swap/log#langlit", new Langlit(), false, true, true);
        setInputConstraints(new InputLogLanglit(this));
    }
}
