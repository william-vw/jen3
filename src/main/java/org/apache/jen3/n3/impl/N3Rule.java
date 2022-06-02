package org.apache.jen3.n3.impl;

import static org.apache.jen3.n3.impl.N3Rule.RuleDirs.BACKWARD;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_Blank;
import org.apache.jen3.graph.n3.N3RuleVisitor;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.impl.skolem.UniqueIdViaKey;
import org.apache.jen3.n3.impl.skolem.UniqueNodeGen;
import org.apache.jen3.reasoner.rulesys.ClauseEntry;
import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;
import org.apache.jen3.util.PrintUtil;

public class N3Rule extends Rule {

	public enum RuleTags {
		HAS_UNIV, NO_DATA
	}

	public enum RuleDirs {
		FORWARD, BACKWARD
	}

	public enum RuleLogics {
		CLASSIC, LINEAR
	}

	private Node headNode;
	private Node bodyNode;

	private N3RuleVisitor ruleVisitor;
	private N3ModelSpec spec;
	private BuiltinConfig builtinConfig;

	private UniqueNodeGen nodeGen;
	private BindingsTable bindingsTable;

	private RuleTags tag;
	private RuleDirs dir;
	private RuleLogics logic;

	public N3Rule(Node head, Node body, N3ModelSpec spec, RuleDirs dir, RuleLogics logic) {
		this.headNode = head;
		this.bodyNode = body;
		this.spec = spec;
		this.dir = dir;
		this.logic = logic;
	}

	public void parse() {
		this.builtinConfig = spec.getBuiltinConfig();

		ruleVisitor = new N3RuleVisitor(builtinConfig, this);

		// TODO ClauseEntry[] - could just as well be represented as cited-formulas
		// but this would break most of Rule superclass
		// temporary bridge w/ original rule engine code

		if (headNode.isCitedFormula()) {
			Node_CitedFormula cf = (Node_CitedFormula) headNode;
			this.head = getClauseEntries(cf);

		} else if (headNode.isVariable())
			headNode = (Node) headNode.visitWith(ruleVisitor, null);

		if (bodyNode.isCitedFormula()) {
			Node_CitedFormula cf = (Node_CitedFormula) bodyNode;
			this.body = getClauseEntries(cf);

			if (builtinConfig.isUseBindingsTable() && ruleVisitor.useBindingsTable())
				bindingsTable = new BindingsTable();
		}
	}

	private ClauseEntry[] getClauseEntries(Node_CitedFormula cf) {
		if (cf.getContents().isEmpty())
			return new ClauseEntry[0];

		else {
			List<ClauseEntry> clauses = ruleVisitor.visitRuleClause(cf, true);
			return clauses.toArray(new ClauseEntry[clauses.size()]);
		}
	}

	public Node getHeadNode() {
		return headNode;
	}

	public Node getBodyNode() {
		return bodyNode;
	}

	public N3ModelSpec getModelSpec() {
		return spec;
	}

	public void setModelSpec(N3ModelSpec modelSpec) {
		this.spec = modelSpec;
	}

	@Override
	public int getNumVars() {
		return ruleVisitor.getNrVars();
	}

	public Node insertRuleVars(Node node) {
		if (node.includesVars())
			return ruleVisitor.pullInVariables(node);
		else
			return node;
	}

	public Node toRuleVar(Node_QuickVariable var) {
		return ruleVisitor.visitQuickVariable(var, null);
	}

	public Node uniqueBlankNode(BindingStack env, Node bnode, Scope scope) {
		return getNodeGen().uniqueBlankNode(env, bnode, scope);
	}

	public Node uniqueSkolem(Node_Blank bnode) {
		return getNodeGen().uniqueSkolem(bnode, spec);
	}

	private UniqueNodeGen getNodeGen() {
		if (nodeGen == null)
			nodeGen = new UniqueIdViaKey();

		return nodeGen;
	}

	public boolean hasBindingsTable() {
		return bindingsTable != null;
	}

	public BindingsTable getBindingsTable() {
		return bindingsTable;
	}

	public boolean hasTag() {
		return tag != null;
	}

	public RuleTags getTag() {
		return tag;
	}

	public void setTag(RuleTags tag) {
		this.tag = tag;
	}

	public RuleDirs getDir() {
		return dir;
	}
	
	@Override
	public boolean isBackward() {
		return dir == RuleDirs.BACKWARD;
	}

	public RuleLogics getLogic() {
		return logic;
	}

	@Override
	public int hashCode() {
		return headNode.hashCode() + 31 * bodyNode.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof N3Rule))
			return false;

		N3Rule r = (N3Rule) o;
		return r.getHeadNode().equals(headNode) && r.getBodyNode().equals(bodyNode);
	}

	private static String[] connectorStrings = { " -> ", " -o ", " <- ", " o- " };

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append("[ ");
		if (name != null) {
			buff.append(name);
			buff.append(": ");
		}

		String conn = connectorStrings[dir.ordinal() * 2 + logic.ordinal()];

		if (this.dir == BACKWARD) {
			buff.append(printHead());
			buff.append("\n").append(conn);
			buff.append(printBody());
		} else {
			buff.append(printBody());
			buff.append("\n").append(conn);
			buff.append(printHead());
		}
		buff.append("]");
		return buff.toString();
	}

	private String printHead() {
		if (head == null || !headNode.isCitedFormula())
			return headNode.toString();
		else
			return print(head);
	}

	private String printBody() {
		if (body == null)
			return bodyNode.toString();
		else
			return print(body);
	}

	private String print(ClauseEntry[] clause) {
		return Arrays.stream(clause).map(t -> PrintUtil.print(t)).collect(Collectors.joining(" "));
	}
}
