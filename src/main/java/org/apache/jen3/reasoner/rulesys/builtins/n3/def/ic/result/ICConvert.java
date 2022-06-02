package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result;

import java.io.Serializable;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.InputConstraint;

public class ICConvert implements Serializable {

	private static final long serialVersionUID = 956261194052433988L;

	private InputConstraint cnstr;

	protected boolean success;
	protected Node node;

	public ICConvert(InputConstraint cnstr) {
		this.cnstr = cnstr;
	}

	public ICConvert(boolean success, InputConstraint cnstr) {
		this.success = success;

		this.cnstr = cnstr;
	}

	public ICConvert(Node n) {
		this.node = n;
		this.success = true;
	}

	public InputConstraint getConstraint() {
		return cnstr;
	}

	public boolean isSuccess() {
		return success;
	}

	public Node getNode() {
		return node;
	}

	public String toString() {
		return node.toString();
	}
}
