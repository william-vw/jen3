package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin.CheckInputListener;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICResult;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICStatementConvert;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint;

public class InputConstraintsDefinition implements Serializable {

	private static final long serialVersionUID = 8547697577169883230L;

	private transient BuiltinDefinition builtin;

	private String uri;
	private Map<BuiltinClauses, StatementInputConstraint> clauses = new TreeMap<>();

	public InputConstraintsDefinition(Resource uri, BuiltinDefinition builtin) {
		if (uri.isURI())
			this.uri = uri.getURI();

		this.builtin = builtin;
	}

	protected InputConstraintsDefinition(BuiltinDefinition builtin, String uri) {
		this.builtin = builtin;
		this.uri = uri;
	}

	public void setBuiltin(BuiltinDefinition builtin) {
		this.builtin = builtin;
	}

	public BuiltinDefinition getBuiltin() {
		return builtin;
	}

	public boolean hasUri() {
		return uri != null;
	}

	public String getUri() {
		return uri;
	}

	public void addClause(BuiltinClauses clause, StatementInputConstraint tr) {
		clauses.put(clause, tr);
	}

	public boolean hasClause(BuiltinClauses clause) {
		return clauses.containsKey(clause);
	}

	public StatementInputConstraint getClause(BuiltinClauses clause) {
		return clauses.get(clause);
	}

	public CheckInputResult checkInput(Node s, Node sb, Node o, Node ob, Graph graph) {
		return checkInput(s, sb, o, ob, null, graph);
	}

	public CheckInputResult checkInput(Node s, Node sb, Node o, Node ob, CheckInputListener listener, Graph graph) {
		CheckInputResult fallthruCause = null;

		// "other clause" is checked in fall-thru part below the loop
		for (BuiltinClauses c : BuiltinClauses.values()) {
			if (!clauses.containsKey(c))
				continue;

			StatementInputConstraint tr = clauses.get(c);

			CheckInputResult res = process(c, sb, ob, tr, graph);

			// e.g., in case of wrong domain, this could throw an exception to stop the
			// reasoning process
			if (listener != null)
				listener.inputResult(builtin.getUri(), s, sb, o, ob, res);

			switch (c) {

			case ACCEPT:
				// if accept clause is not met, stop processing rule here
				if (!res.isSuccess())
					return res;
				fallthruCause = res;
				break;

			case DOMAIN:
				// if domain clause is successful, return the result
				// if not, we'll fall through to the "other" clause, if any
				if (res.isSuccess())
					return res;
				fallthruCause = res;
				break;

			// accept was successful, but domain was not
			case NOT_BOUND:
				// set cause of falling through to "other"
				res.setFallthruCause(fallthruCause);
				// return whatever "other" clause determined
				return res;
				
			default:
				break;
			}
		}
		
		// shouldn't get here, if every input constraint is well defined
		return null;
	}

	private CheckInputResult process(BuiltinClauses clause, Node s, Node o, StatementInputConstraint ic, Graph graph) {
		ICResult result = null;
		switch (clause) {

		case ACCEPT:
		case NOT_BOUND:
			// only need to check for these clauses
			result = ic.check(s, o, graph);
			if (result.isSuccess())
				return new CheckInputResult(clause, true);
			break;

		case DOMAIN:
			// in case of domain, need to convert
			result = ic.convert(s, o, graph);
			if (result.isSuccess())
				return new CheckInputResult(clause, true, (ICStatementConvert) result);
			break;

		default:
			break;
		}

		return new CheckInputResult(clause, false, result.getTraces());
	}

	public String toString() {
		return clauses.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining("\n"));
	}

	public static class CheckInputResult {

		private BuiltinClauses clause;
		private boolean success;
		private List<ICTrace> failed;
		private ICStatementConvert input;

		private CheckInputResult fallthruCause;

		private CheckInputResult(BuiltinClauses clause, boolean success) {
			this.clause = clause;
			this.success = success;
		}

		private CheckInputResult(BuiltinClauses clause, boolean success, List<ICTrace> failed) {
			this.clause = clause;
			this.success = success;
			this.failed = failed;
		}

		private CheckInputResult(BuiltinClauses clause, boolean success, ICStatementConvert input) {
			this.clause = clause;
			this.success = success;
			this.input = input;
		}

		public CheckInputResult(BuiltinClauses clause, boolean success, CheckInputResult fallthruCause) {
			this.clause = clause;
			this.success = success;
			this.fallthruCause = fallthruCause;
		}

		public BuiltinClauses getClause() {
			return clause;
		}

		public boolean isSuccess() {
			return success;
		}

		public List<ICTrace> getFailed() {
			return failed;
		}

		public ICStatementConvert getInput() {
			return input;
		}

		public boolean fellThru() {
			return fallthruCause != null;
		}

		public void setFallthruCause(CheckInputResult fallthruCause) {
			this.fallthruCause = fallthruCause;
		}

		public CheckInputResult getFallThruCause() {
			return fallthruCause;
		}

		@Override
		public String toString() {
			return toString(false);
		}

		public String toString(boolean mini) {
			return clause + " " + (success ? "succeeded" : "failed")
					+ (!mini && failed != null
							? "\n" + failed.stream().map(t -> t.toString()).collect(Collectors.joining("\n"))
							: "");
		}
	}
}
