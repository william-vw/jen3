package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICBase;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.InputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICCheck;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICStatementConvert;

public abstract class StatementInputConstraint extends ICBase {

	private static final long serialVersionUID = -7493850730207072524L;

	public enum StatementICTypes {
		ONE_OF, BOOL, SO;
	}

	public enum StmtElements {
		SUBJECT, OBJECT;

		public InputConstraint get(InputConstraint subject, InputConstraint object) {
			switch (this) {
			case SUBJECT:
				return subject;
			case OBJECT:
				return object;
			default:
				return null;
			}
		}

		public Node get(Node s, Node o) {
			switch (this) {
			case SUBJECT:
				return s;
			case OBJECT:
				return o;
			default:
				return null;
			}
		}
	}

	private transient InputConstraintsDefinition inputCnstr;
	private StatementICTypes type;

	public StatementInputConstraint(InputConstraintsDefinition inputCnstr, StatementICTypes type) {
		this.inputCnstr = inputCnstr;
		this.type = type;
	}

	public InputConstraintsDefinition getInputConstraints() {
		return inputCnstr;
	}
	
	public BuiltinDefinition getBuiltin() {
		return inputCnstr.getBuiltin();
	}

	public StatementICTypes getType() {
		return type;
	}
	
	public ICTrace createTrace(StmtElements target, Graph g) {
		return g.getN3Config().getBuiltinConfig().createICTrace(target);
	}

	public abstract ICCheck check(Node s, Node o, Graph graph);

	public abstract ICStatementConvert convert(Node s, Node o, Graph graph);
}
