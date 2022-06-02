package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result;

import java.util.List;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;

public class ICCheck extends ICResult {

	public ICCheck(boolean success) {
		super(success);
	}

	public ICCheck(boolean success, ICTrace trace) {
		super(success, trace);
	}

	public ICCheck(boolean success, List<ICTrace> traces) {
		super(success, traces);
	}
}
