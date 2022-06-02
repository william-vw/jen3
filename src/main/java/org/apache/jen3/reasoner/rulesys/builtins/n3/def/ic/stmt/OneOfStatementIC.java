package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICCheck;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICStatementConvert;

public class OneOfStatementIC extends StatementInputConstraint {

	private static final long serialVersionUID = 6590874195790626039L;

	private List<StatementInputConstraint> types = new ArrayList<>();

	public OneOfStatementIC(InputConstraintsDefinition inputCnstr, StatementInputConstraint... types) {
		super(inputCnstr, StatementICTypes.ONE_OF);

		this.types = new ArrayList<>(Arrays.asList(types));
	}

	public void add(StatementInputConstraint type) {
		this.types.add(type);
	}

	public List<StatementInputConstraint> getElements() {
		return types;
	}

	@Override
	public ICCheck check(Node s, Node o, Graph graph) {
		List<ICTrace> failed = new ArrayList<>();

		for (StatementInputConstraint type : types) {
			ICCheck checked = type.check(s, o, graph);
			if (checked.isSuccess())
				return checked;
			else
				failed.addAll(checked.getTraces());
		}

		return new ICCheck(false, failed);
	}

	@Override
	public ICStatementConvert convert(Node s, Node o, Graph graph) {
		List<ICTrace> failed = new ArrayList<>();

		for (StatementInputConstraint type : types) {
			ICStatementConvert converted = type.convert(s, o, graph);
			if (converted.isSuccess()) {
				return converted;

			} else
				failed.addAll(converted.getTraces());
		}

		return new ICStatementConvert(false, failed, this);
	}

	public String toString() {
		return "{ oneOf " + types + " }";
	}
}
