package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.graph.Difference;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class GraphDifference extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public GraphDifference() {
        super("http://www.w3.org/2000/10/swap/graph#difference", new Difference(), false, true, true);
        setInputConstraints(new InputGraphDifference(this));
    }
}
