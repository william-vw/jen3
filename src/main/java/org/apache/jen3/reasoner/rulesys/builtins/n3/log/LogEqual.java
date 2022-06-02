package org.apache.jen3.reasoner.rulesys.builtins.n3.log;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.reasoner.rulesys.impl.RuleUtil;

public abstract class LogEqual extends LogCompare {

	protected LogEqual(boolean inverse) {
		super(inverse);
	}

	@Override
	protected boolean match(Node sb, Node ob, BindingStack env, Graph graph) {
		return RuleUtil.match(sb, ob, RuleUtil.configDefault, env);
	}
}
