package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.file.ListFiles;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class FileListFiles extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public FileListFiles() {
        super("http://www.w3.org/2000/10/swap/file#listFiles", new ListFiles(), true, true, true);
        setInputConstraints(new InputIriAndFormula(this));
    }
}
