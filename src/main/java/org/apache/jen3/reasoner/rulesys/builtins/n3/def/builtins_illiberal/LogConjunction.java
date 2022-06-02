package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Conjunction;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogConjunction extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogConjunction() {
        super("http://www.w3.org/2000/10/swap/log#conjunction", new Conjunction(), false, true, true);
        setInputConstraints(new InputLogConjunction(this));
    }
}
