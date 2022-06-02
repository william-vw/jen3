package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint.StmtElements;

public interface ICTrace {

	public static ICTraceFactory factory = null;
	
	public void push(InputConstraint tr, Node node);

	public int mark();

	public void pop();
	
	public void rewind(int mark);
	
	public interface ICTraceFactory {
		
		public ICTrace create(StmtElements target);
	}
}
