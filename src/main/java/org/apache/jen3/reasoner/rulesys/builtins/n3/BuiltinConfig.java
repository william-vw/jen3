package org.apache.jen3.reasoner.rulesys.builtins.n3;

import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin.BuiltinEvaluationListener;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin.BuiltinInputLogger;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin.CheckInputListener;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinitionMarshaller.BuiltinMarshallTypes;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace.ICTraceFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTraceImpl.ICTraceImplFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint.StmtElements;

public class BuiltinConfig {

	public static String defaultBuiltinDefPath = "etc/builtins/builtins_illiberal.n3";

	public static String defaultBuiltinPck = "org.apache.jen3.reasoner.rulesys.builtins.n3.def.";
	public static String customBuiltinPck = "org.apache.jen3.reasoner.rulesys.builtins.n3.def.custom.";

	private boolean initialized = false;

	private N3ModelSpec spec;
	private String builtinDefPath = defaultBuiltinDefPath;
	private String builtinDefPck = customBuiltinPck; // per default, use "custom" package for
														// compilation

	// whether to marshall builtin definitions (serialize or compile) to speed up
	// loading (serializing is simpler, but only reduces builtin load time ca. 5x)
	private BuiltinMarshallTypes marshallBuiltins = BuiltinMarshallTypes.COMPILE;

	// pertain to downloads by N3 builtins (e.g., log:content, log:semantics)
	private int downloadTimeout = 10000;
	private boolean printDownloads = true;
	// whether to use a bindings table in case of "resource-intensive" builtins
	// these also need to have "resourceIntensive" property set in builtins.n3
	// (e.g., log:semantics, log:content)
	private boolean useBindingsTable = true;

	private BuiltinSet builtinSet = new BuiltinSet();
	private BuiltinEvaluationListener evalListener;
	private CheckInputListener inputListener;

	private boolean traceInputCheck;
	private ICTraceFactory traceFactory;

	public BuiltinConfig(N3ModelSpec spec) {
		this.spec = spec;
	}

	public BuiltinConfig(N3ModelSpec spec, String builtinDefPath, String builtinDefPck,
			int downloadTimeout, boolean printDownloads, boolean useBindingsTable,
			BuiltinMarshallTypes marshallBuiltins, boolean traceInputCheck) {

		this.spec = spec;
		this.builtinDefPath = builtinDefPath;
		this.builtinDefPck = builtinDefPck;
		this.downloadTimeout = downloadTimeout;
		this.printDownloads = printDownloads;
		this.useBindingsTable = useBindingsTable;
		this.marshallBuiltins = marshallBuiltins;
		this.traceInputCheck = traceInputCheck;
	}

	public void initialize() {
		if (initialized)
			return;

		initialized = true;
		BuiltinSet.load(this, builtinSet);

		setInputListener(new BuiltinInputLogger(spec));

		traceFactory = new ICTraceImplFactory();
//		traceFactory = (traceInputCheck ? new ICTraceImplFactory() : new ICTraceDummyFactory());
	}

	public String getBuiltinDefPath() {
		return builtinDefPath;
	}

	public boolean isDefaultBuiltinDefPath() {
		return builtinDefPath.equals(defaultBuiltinDefPath);
	}

	public void setBuiltinDefPath(String builtinDefPath) {
		this.builtinDefPath = builtinDefPath;
	}

	public String getBuiltinDefPck() {
		return builtinDefPck;
	}

	public void setBuiltinDefPck(String builtinDefPck) {
		this.builtinDefPck = builtinDefPck;
	}

	public int getDownloadTimeout() {
		return downloadTimeout;
	}

	public void setDownloadTimeout(int downloadTimeout) {
		this.downloadTimeout = downloadTimeout;
	}

	public boolean isPrintDownloads() {
		return printDownloads;
	}

	public void setPrintDownloads(boolean printDownloads) {
		this.printDownloads = printDownloads;
	}

	public boolean isUseBindingsTable() {
		return useBindingsTable;
	}

	public void setUseBindingsTable(boolean useBindingsTable) {
		this.useBindingsTable = useBindingsTable;
	}

	public BuiltinMarshallTypes getMarshallBuiltins() {
		return marshallBuiltins;
	}

	public void setMarshallBuiltins(BuiltinMarshallTypes marshallBuiltins) {
		this.marshallBuiltins = marshallBuiltins;
	}

	public void traceInputCheck(boolean traceInputCheck) {
		this.traceInputCheck = traceInputCheck;
	}

	public boolean traceInputCheck() {
		return traceInputCheck;
	}

	public ICTrace createICTrace(StmtElements target) {
		return traceFactory.create(target);
	}

	public BuiltinSet getSet() {
		return builtinSet;
	}

	public void setEvalListener(BuiltinEvaluationListener evalListener) {
		this.evalListener = evalListener;
	}

	public BuiltinEvaluationListener getEvalListener() {
		return evalListener;
	}

	public boolean hasEvalListener() {
		return evalListener != null;
	}

	public CheckInputListener getInputListener() {
		return inputListener;
	}

	public void setInputListener(CheckInputListener inputListener) {
		this.inputListener = inputListener;
	}

	public boolean hasInputListener() {
		return inputListener != null;
	}

	public BuiltinConfig copy() {
		return new BuiltinConfig(spec, builtinDefPath, builtinDefPck, downloadTimeout,
				printDownloads, useBindingsTable, marshallBuiltins, traceInputCheck);
	}
}
