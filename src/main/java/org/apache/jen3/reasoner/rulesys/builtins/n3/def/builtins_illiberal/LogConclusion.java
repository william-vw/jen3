package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Conclusion;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogConclusion extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogConclusion() {
        super("http://www.w3.org/2000/10/swap/log#conclusion", new Conclusion(), false, true, true);
        setInputConstraints(new InputFormulaAndFormulaOrVariable(this));
    }
}
