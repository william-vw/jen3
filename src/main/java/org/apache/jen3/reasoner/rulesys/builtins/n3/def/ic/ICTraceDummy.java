package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint.StmtElements;

public class ICTraceDummy implements ICTrace {

	@Override
	public void push(InputConstraint tr, Node node) {
	}

	@Override
	public int mark() {
		return 0;
	}

	@Override
	public void pop() {
	}

	@Override
	public void rewind(int mark) {
	}

	public static class ICTraceDummyFactory implements ICTraceFactory {

		private ICTrace instance = new ICTraceDummy();

		@Override
		public ICTrace create(StmtElements target) {
			return instance;
		}
	}

	@Override
	public String toString() {
		return "(to see the input trace, enable 'traceInputCheck' in N3ModelSpec.builtinConfig)";
	}
}
