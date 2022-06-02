package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.SemanticsOrError;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogSemanticsOrError extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogSemanticsOrError() {
        super("http://www.w3.org/2000/10/swap/log#semanticsOrError", new SemanticsOrError(), true, true, true);
        setInputConstraints(new InputLogSemanticsOrError(this));
    }
}
