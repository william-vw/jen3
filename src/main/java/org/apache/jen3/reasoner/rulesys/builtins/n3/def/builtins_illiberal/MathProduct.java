package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.math.Product;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class MathProduct extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public MathProduct() {
        super("http://www.w3.org/2000/10/swap/math#product", new Product(), false, true, true);
        setInputConstraints(new InputConcreteNumberListAndNumberOrVariable(this));
    }
}
