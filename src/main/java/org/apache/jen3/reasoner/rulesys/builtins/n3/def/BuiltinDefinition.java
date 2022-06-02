package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.io.Serializable;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin.CheckInputListener;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition.CheckInputResult;

public class BuiltinDefinition implements Serializable {

	private static final long serialVersionUID = 8850763941825804256L;

	public static enum ProcessResults {
		SUCCESS, FAILED
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String uri;
	private transient N3Builtin impl;
	private InputConstraintsDefinition inputCnstr;
	private boolean resourceIntensive = false;
//	private boolean universal = false;
	private boolean instantiate = true;
	private boolean isStatic = true;

	public BuiltinDefinition(Resource uri) {
		this.uri = uri.getURI();
	}

	protected BuiltinDefinition(String uri, N3Builtin impl, boolean resourceIntensive, boolean instantiate,
			boolean isStatic) {

		this.uri = uri;

		if (impl != null) {
			this.impl = impl;
			impl.setDefinition(this);
		}

		this.resourceIntensive = resourceIntensive;
		this.instantiate = instantiate;
		this.isStatic = isStatic;
	}

	public String getUri() {
		return uri;
	}

	public void setInputConstraints(InputConstraintsDefinition inputCnstr) {
		this.inputCnstr = inputCnstr;
	}

	public InputConstraintsDefinition getInputConstraints() {
		return inputCnstr;
	}

	public N3Builtin getImpl() {
		return impl;
	}

	public boolean hasImpl() {
		return impl != null;
	}

	public void setImpl(N3Builtin impl) {
		this.impl = impl;
	}

	// supports hack to avoid resource-intensive builtins to be fired more than
	// once (e.g., log:content, log:semantics)
	// requires the rule to have a "bindings-table" to keep bindings that have
	// already been tried
	// (also requires useBindingsTable to be set to true in N3ModelSpec)

	public boolean isResourceIntensive() {
		return resourceIntensive;
	}

	public void setResourceIntensive(boolean resourceIntensive) {
		this.resourceIntensive = resourceIntensive;
	}

	// whether this builtin has a corresponding implementation (java class)

	public boolean isInstantiate() {
		return instantiate;
	}

	public void setInstantiate(boolean instantiate) {
		this.instantiate = instantiate;
	}

	// whether this builtin has a fixed theory-box or not
	// (most of them will; current exceptions include log:implies, log:outputString)

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public CheckInputResult checkInput(Node s, Node sb, Node o, Node ob, Graph graph) {
		return inputCnstr.checkInput(s, sb, o, ob, null, graph);
	}

	public CheckInputResult checkInput(Node s, Node sb, Node o, Node ob, CheckInputListener listener, Graph graph) {
		return inputCnstr.checkInput(s, sb, o, ob, listener, graph);
	}

	public String toString() {
		return uri + "\n" + inputCnstr;
	}
}
