package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.Content;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogContent extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogContent() {
        super("http://www.w3.org/2000/10/swap/log#content", new Content(), true, true, true);
        setInputConstraints(new InputLogContent(this));
    }
}
