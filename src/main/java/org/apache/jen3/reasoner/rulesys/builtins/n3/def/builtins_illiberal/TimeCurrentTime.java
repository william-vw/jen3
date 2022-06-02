package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.CurrentTime;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeCurrentTime extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeCurrentTime() {
        super("http://www.w3.org/2000/10/swap/time#currentTime", new CurrentTime(), false, true, true);
        setInputConstraints(new InputTimeCurrentTime(this));
    }
}
