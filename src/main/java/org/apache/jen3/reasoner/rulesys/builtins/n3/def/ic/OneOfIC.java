package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public class OneOfIC extends InputConstraint {

	private static final long serialVersionUID = -9038753731201123650L;

	private List<InputConstraint> types = new ArrayList<>();

	public OneOfIC() {
		super(DefaultICs.ONE_OF);
	}

	public OneOfIC(InputConstraint... trs) {
		this.types = new ArrayList<>(Arrays.asList(trs));
	}

	public void add(InputConstraint tr) {
		types.add(tr);
	}

	public List<InputConstraint> getElements() {
		return types;
	}

	@Override
	protected boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		int mark = trace.mark();
		return types.stream().anyMatch(type -> {
			boolean ret = type.check(n, id, graph, trace);
			trace.rewind(mark);

			return ret;
		});
	}

	// don't do separate check (avoid going over elements twice)
	// instead, just do all work in one go, incl. checking

	@Override
	protected ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		int mark = trace.mark();
		for (InputConstraint type : types) {
			ICConvert input = type.convert(n, id, graph, trace);
			trace.rewind(mark);

			if (input.isSuccess())
				return input;
		}

		return noMatch;
	}

	public String toString() {
		return "oneOf " + types;
	}
}
