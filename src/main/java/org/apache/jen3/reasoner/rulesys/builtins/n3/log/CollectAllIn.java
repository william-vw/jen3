package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import static org.apache.jen3.reasoner.rulesys.builtins.n3.log.UniversalQuantification.MatchGoals.NO_NEG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;

public class CollectAllIn extends UniversalQuantification {

	@Override
	protected boolean match(Node sb, Node ob, BindingStack env, Graph graph, Finder continuation) {
		Node_Collection list = (Node_Collection) sb;

		Node_Variable select = (Node_Variable) list.getElements().get(0);
		Node_CitedFormula where = (Node_CitedFormula) list.getElements().get(1);
		Node collect = list.getElements().get(2);

		List<Triple> clauses = where.getContents().getGraph().find().toList();

		CollectNodes collector = new CollectNodes(select, collect, env);

		Graph target = getTargetGraph(ob, graph);

		boolean match = match(clauses, collector, env, target, continuation);
		boolean ret = (match && collector.finish());
		if (!ret)
			return false;

		// if collect is a variable, assign our collected list to it
		if (collect.isVariable()) {
			Node_Collection collectList = NodeFactory.createCollection(collector.getCollected());
			env.bind((Node_Variable) collect, collectList);
		}

		return true;
	}

	private boolean match(List<Triple> clauses, CollectNodes collector, BindingStack env,
			Graph graph, Finder continuation) {
		
		MatchContext mCtx = new MatchContext(RuleUtil.toTriplePatterns(clauses), env, graph);

		return matchClauses(mCtx.getTripleSets(), mCtx, NO_NEG, collector, continuation);
	}

	private class CollectNodes implements Function<Void, Boolean> {

		private Node_Variable select;
		private BindingStack env;

		private List<Node> cmpList;
		private List<Node> curList = new ArrayList<>();

		public CollectNodes(Node_Variable select, Node collect, BindingStack env) {
			this.select = select;
			this.env = env;

			if (collect.isCollection())
				cmpList = ((Node_Collection) collect).getElements();
		}

		public List<Node> getCollected() {
			return curList;
		}

		@Override
		public Boolean apply(Void v) {
			Node node = env.ground(select);
			curList.add(node);

			// if collect is a list as well
			if (cmpList != null) {
				if (curList.size() <= cmpList.size())
					// if smaller than collect's list, compare the two
					return (cmpList.subList(0, curList.size()).equals(curList));
				// if our list exceeds collect's list size, return false
				else
					return false;
			} else
				return true;
		}

		public boolean finish() {
			// if collect is a list as well
			if (cmpList != null) {
				// up until now, we know that list2 is a prefix of list
				// will only be equal if they are the exact same size
				return cmpList.size() == curList.size();
			} else
				return true;
		}
	}
}
