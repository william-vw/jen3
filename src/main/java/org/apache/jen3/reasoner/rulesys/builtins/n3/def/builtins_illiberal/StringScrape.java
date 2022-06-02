package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.string.Scrape;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class StringScrape extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public StringScrape() {
        super("http://www.w3.org/2000/10/swap/string#scrape", new Scrape(), false, true, true);
        setInputConstraints(new InputStringScrape(this));
    }
}
