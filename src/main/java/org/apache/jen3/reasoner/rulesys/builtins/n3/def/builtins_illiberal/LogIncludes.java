package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Includes;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogIncludes extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogIncludes() {
        super("http://www.w3.org/2000/10/swap/log#includes", new Includes(), false, true, true);
        setInputConstraints(new InputFormulaOrBaseAndFormula(this));
    }
}
