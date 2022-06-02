package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.crypto.Sha;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class CryptoSha extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public CryptoSha() {
        super("http://www.w3.org/2000/10/swap/crypto#sha", new Sha(), false, true, true);
        setInputConstraints(new InputStringAndStringOrVariable(this));
    }
}
