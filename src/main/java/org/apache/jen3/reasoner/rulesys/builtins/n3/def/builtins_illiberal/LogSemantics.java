package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Semantics;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogSemantics extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogSemantics() {
        super("http://www.w3.org/2000/10/swap/log#semantics", new Semantics(), true, true, true);
        setInputConstraints(new InputIriAndFormula(this));
    }
}
