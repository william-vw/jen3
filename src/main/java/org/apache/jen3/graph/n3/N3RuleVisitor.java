package org.apache.jen3.graph.n3;

import static org.apache.jen3.n3.N3MistakeTypes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jen3.graph.BlankNodeId;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeTypes;
import org.apache.jen3.graph.Node_Blank;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.n3.impl.N3Rule;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.ClauseEntry;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.vocabulary.N3Log;
import org.apache.jen3.vocabulary.RDF;

/**
 * Replaces quick-vars, quantified-vars with rule-variables. Also gathers stats
 * relevant to the N3 rule.
 * 
 * 
 * @author wvw
 *
 */

public class N3RuleVisitor extends InsertVarVisitor {

	private int nrVars = 0;

	private boolean inBody;
	private Map<String, Node> localVarMap = new HashMap<>();
	private Map<String, Node> globalVarMap = new HashMap<>();

	private boolean pullingIn = false;

	private N3Rule rule;
	private BuiltinSet builtinSet;

	// state kept per call of visitRuleClause
	private boolean useBindingsTable = false;

	public N3RuleVisitor(BuiltinConfig builtinConfig, N3Rule rule) {
		this.rule = rule;

		this.builtinSet = builtinConfig.getSet();
	}

	public List<ClauseEntry> visitRuleClause(Node_CitedFormula cf, boolean body) {
		this.inBody = body;

		this.useBindingsTable = false;

		// visiting new clause ; so clear local variables
		localVarMap.clear();

		List<ClauseEntry> clauses = new ArrayList<>();
		cf.getContents().listStatements().forEachRemaining(stmt -> visit(stmt.asTriple(), clauses));

		return clauses;
	}

	public boolean useBindingsTable() {
		return useBindingsTable;
	}

	private void visit(Triple t, List<ClauseEntry> clauses) {
		// support for linear logic
		Node s = t.getSubject();
		Node p = t.getPredicate();
		Node o = t.getObject();

		if (o.isURI() && o.getURI().equals(N3Log.StableTruth.getURI())) {

			// check whether stable truth is properly used
			if (inBody && p.isURI() && p.getURI().equals(RDF.type.getURI()) && s.isCitedFormula()) {
				Node_CitedFormula cf = (Node_CitedFormula) s;

				// treat contents of quoted graph as regular statements in body,
				// but tagged as stable truth
				cf.getContents().listStatements().forEachRemaining(stmt -> {
					TriplePattern tp = visitTriple(stmt.asTriple());
					tp.setStableTruth(true);

					clauses.add(tp);
				});

				// possibly flag any wrong usage of stable truth
			} else if (rule.getModelSpec().hasFeedbackFor(INFER_MISUSE_STABLE_TRUTH))
				rule.getModelSpec().getFeedback(INFER_MISUSE_STABLE_TRUTH).doDefaultAction(rule);

		} else {
			TriplePattern tp = visitTriple(t);
			clauses.add(tp);
		}
	}

	private TriplePattern visitTriple(Triple t) {
		Node[] nodes = { t.getSubject(), t.getPredicate(), t.getObject() };
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = nodes[i].visitWith(this, null);

			if (i == 1 && inBody && nodes[i].isConcrete() && builtinSet.isBuiltin(nodes[i])) {
				if (builtinSet.getBuiltin(nodes[i]).isResourceIntensive())
					useBindingsTable = true;
			}
		}

		return new TriplePattern(nodes[0], nodes[1], nodes[2]);
	}

	@Override
	protected TriplePattern visit(Triple t, Object data) {
		Node subject = t.getSubject();
		Node predicate = t.getPredicate();
		Node object = t.getObject();

		return new TriplePattern(subject.visitWith(this, data), predicate.visitWith(this, data),
				object.visitWith(this, data));
	}

	public Node pullInVariables(Node n) {
		if (n.isContainer() || n.isVariable()) {
			this.pullingIn = true;
			n = n.visitWith(this, null);
			this.pullingIn = false;
		}

		return n;
	}

	@Override
	public Node visitQuickVariable(Node_QuickVariable it, Object data) {
//		System.out.println("quick-var: " + it);
		return var(it, it.getName(), true);
	}

	@Override
	public Node visitQuantifiedVariable(Node_QuantifiedVariable it, Object data) {
		Node_Quantifier qier = it.getQuantifier();
		if (qier.isUniversal())
			return var(it, it.getName(), true);
		else
			return it;
	}

	@Override
	public Node visitBlank(Node_Blank it, BlankNodeId id, Object data) {
		// only convert blank nodes when visiting the original clause
//		if (!pullingIn || inNestedFormula)
		if (!pullingIn)
			return var(it, it.getBlankNodeLabel(), false);
		else
			return it;
	}

	@Override
	public Node visitRuleVariable(Node_RuleVariable it, Object data) {
//		System.out.println("rule-var: " + it);

		// mint a new rule-variable
		// e.g., log:conclusion will need new ones after the data was pulled into the
		// rule (and hence rule variables were inserted)

		return var(it, it.getName(), !it.getOriginal().isBlank());
	}

	private Node var(Node n, String name, boolean univ) {
		if (!univ && !inBody)
			return n;

		return var(n, name, (univ ? globalVarMap : localVarMap));
	}

	private Node var(Node n, String name, Map<String, Node> varMap) {
		Node var = null;
		if (varMap.containsKey(name)) {
			var = varMap.get(name);

		} else {
			var = newVar(n, name);
			varMap.put(name, var);
		}

		return var;
	}

	private Node newVar(Node n, String name) {
		if (n.getType() == NodeTypes.RULEVAR)
			System.err.println("ERROR shoulnd't be replacing a rule-var with another rule-var");

		Node_RuleVariable var = new Node_RuleVariable(name, nrVars++);
		var.setOriginal(n);

		return var;
	}

	@Override
	public Node visitCitedFormula(Node_CitedFormula it, Object data) {
		Map<String, Node> priorVarMap = localVarMap;
		this.localVarMap = new HashMap<>();

		Node ret = super.visitCitedFormula(it, data);

		this.localVarMap = priorVarMap;

		return ret;
	}

	public int getNrVars() {
		return nrVars;
	}
}