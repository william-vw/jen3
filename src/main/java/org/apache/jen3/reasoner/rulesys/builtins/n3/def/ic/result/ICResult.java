package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;

public class ICResult {

	private boolean success;
	private List<ICTrace> traces;

	public ICResult(boolean success) {
		this.success = success;
	}

	public ICResult(boolean success, ICTrace trace) {
		this.success = success;
		this.traces = Arrays.asList(trace);
	}

	public ICResult(boolean success, List<ICTrace> traces) {
		this.success = success;
		this.traces = traces;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<ICTrace> getTraces() {
		return traces;
	}

	public String toString() {
		if (traces == null || traces.isEmpty())
			return "";
		else
			return traces.stream().map(trace -> trace.toString()).collect(Collectors.joining("\n")) + "\n";
	}
}
