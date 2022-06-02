package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.vocabulary.BuiltinNS;

public class Cardinality implements Serializable {

	private static final long serialVersionUID = 6015821040733168384L;

	public static List<Cardinality> stack = new ArrayList<>();

	private Cardinalities type;
	private int value;

	private transient int curId = -1;
	private transient Set<Node> nodes = new HashSet<>();
	private transient ICTrace trace;

	public Cardinality(Cardinalities type, int value) {
		this.type = type;
		this.value = value;
	}

	public Cardinalities getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	public boolean check(Node n, int id, ICTrace trace) {
		if (id != curId) {
			curId = id;
			nodes.clear();

			this.trace = trace;
			stack.add(this);
		}

		nodes.add(n);
		return matches(nodes.size(), false);
	}

	@SuppressWarnings("incomplete-switch")
	public boolean matches(int cnt, boolean total) {
		switch (type) {

		case MAX:
			return cnt <= value;

		default:
			if (total) {
				switch (type) {

				case EXACT:
					return cnt == value;

				case MIN:
					return cnt < value;
				}
			}
			
			return true;
		}
	}

	public static CardinalityCheck checkClause() {
		Optional<Cardinality> noMatch = stack.stream().filter(c -> !c.finalCheck()).findFirst();
		stack.clear();

		if (noMatch.isPresent())
			return new CardinalityCheck(false, noMatch.get().trace);
		else
			return new CardinalityCheck(true);
	}

	public boolean finalCheck() {
		int cnt = nodes.size();
		// Log.info(getClass(), "finalCheck? id=" + curId + " , " + type + " - " +
		// nodes);

		switch (type) {

		case MIN:
			return cnt >= value;

		case EXACT:
			return cnt == value;

		default:
			return true;
		}
	}

	public String toString() {
		return type + " = " + value;
	}

	public static class CardinalityCheck {

		private boolean success;
		private ICTrace trace;

		public CardinalityCheck(boolean success) {
			this.success = success;
		}

		public CardinalityCheck(boolean success, ICTrace trace) {
			this.success = success;
			this.trace = trace;
		}

		public boolean isSuccess() {
			return success;
		}

		public ICTrace getTrace() {
			return trace;
		}
	}
}
