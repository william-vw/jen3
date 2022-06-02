package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.BuiltinTripleSet.SingletonTripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

public abstract class LogCompare extends LogBuiltin {

	private boolean inverse = false;

	protected LogCompare(boolean inverse) {
		this.inverse = inverse;
	}

	@Override
	public TripleSet toTripleSet(TriplePattern clause, Graph graph, Finder continuation) {
		return new CompareTripleSet(clause, definition, graph);
	}

	protected class CompareTripleSet extends SingletonTripleSet {

		public CompareTripleSet(TriplePattern clause, BuiltinDefinition definition, Graph graph) {
			super(clause, definition, graph);
		}

		@Override
		protected Triple builtinTriple(Node sb, Node ob, BindingStack env) {
			boolean ret = match(sb, ob, env, graph);
			
			if ((!inverse && ret) || (inverse && !ret))
				return instantiate();
			else
				return null;
		}
	}

	protected abstract boolean match(Node sb, Node ob, BindingStack env, Graph graph);
}
