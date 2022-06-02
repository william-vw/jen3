package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.ParsedAsN3;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogParsedAsN3 extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogParsedAsN3() {
        super("http://www.w3.org/2000/10/swap/log#parsedAsN3", new ParsedAsN3(), false, true, true);
        setInputConstraints(new InputLogParsedAsN3(this));
    }
}
