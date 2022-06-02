package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.ScrapeAll;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringScrapeAll extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringScrapeAll() {
        super("http://www.w3.org/2000/10/swap/string#scrapeAll", new ScrapeAll(), false, true, true);
        setInputConstraints(new InputStringScrapeAll(this));
    }
}
