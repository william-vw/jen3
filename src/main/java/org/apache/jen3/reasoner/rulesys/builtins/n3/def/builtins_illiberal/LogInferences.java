package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Inferences;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogInferences extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogInferences() {
        super("http://www.w3.org/2000/10/swap/log#inferences", new Inferences(), false, true, true);
        setInputConstraints(new InputFormulaAndFormulaOrVariable(this));
    }
}
