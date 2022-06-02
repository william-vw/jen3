package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.NotIncludes;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogNotIncludes extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogNotIncludes() {
        super("http://www.w3.org/2000/10/swap/log#notIncludes", new NotIncludes(), false, true, true);
        setInputConstraints(new InputFormulaOrBaseAndFormula(this));
    }
}
