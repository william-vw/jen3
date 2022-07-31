package org.apache.jen3.reasoner.rulesys.impl;

import static org.apache.jen3.graph.NodeTypes.RULEVAR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.graph.n3.Quantifiers;
import org.apache.jen3.n3.impl.N3Rule;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.Functor;
import org.apache.jen3.reasoner.rulesys.PatternTripleSet;
import org.apache.jen3.reasoner.rulesys.TripleSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jena.atlas.logging.Log;

public class RuleUtil {

	public static RuleMatchConfig configDefault = new RuleMatchConfig().onlyBindVars(false);
	public static RuleMatchConfig configOnlyBindVars = new RuleMatchConfig().onlyBindVars(true);

	public static boolean match(TriplePattern pattern, Triple triple, BindingStack env) {
		return match(pattern, triple, false, env);
	}

	/**
	 * Test if a TriplePattern matches a Triple in the given binding environment. If
	 * it does then the binding environment is modified the reflect any additional
	 * bindings.
	 * 
	 * @param onlyBindVars focus solely on binding variables (no equality checking)
	 * @return true if the pattern matches the triple
	 */
	public static boolean match(TriplePattern pattern, Triple triple, boolean onlyBindVars,
			BindingStack env) {

		RuleMatchConfig config = (onlyBindVars ? RuleUtil.configOnlyBindVars
				: RuleUtil.configDefault);

		env.push();
		boolean matchOK = match(pattern.getSubject(), triple.getSubject(), config, env)
				&& match(pattern.getObject(), triple.getObject(), config, env)
				&& match(pattern.getPredicate(), triple.getPredicate(), config, env);
		if (matchOK) {
			env.commit();
			return true;
		} else {
			env.unwind();
			return false;
		}
	}

	public static boolean match(Triple pattern, Triple triple, RuleMatchConfig config,
			BindingStack env) {
		env.push();
		boolean matched = match(pattern.getPredicate(), triple.getPredicate(), config, env)
				&& match(pattern.getObject(), triple.getObject(), config, env)
				&& match(pattern.getSubject(), triple.getSubject(), config, env);
		if (matched) {
			env.commit();
			return true;
		} else {
			env.unwind();
			return false;
		}
	}

	public static boolean singleMatch(Node pattern, Node node, RuleMatchConfig config,
			BindingStack env) {
		env.push();
		boolean matchOK = RuleUtil.match(pattern, node, RuleUtil.configDefault, env);
		if (matchOK)
			env.commit();
		else
			env.unwind();

		return matchOK;
	}

	/**
	 * Test if a pattern Node matches a Triple Node in the given binding
	 * environment. If it does then the binding environment is modified the reflect
	 * any additional bindings.
	 * 
	 * @param configOnlyBindVars focus solely on binding variables (no equality
	 *                           checking)
	 * @return true if the pattern matches the node
	 */
	public static boolean match(Node pattern, Node node, RuleMatchConfig config, BindingStack env) {
//		System.out.println("match: " + pattern + " <> " + node);

		boolean ret = true;

		// if same objects, no need to do more complex matching
		if (pattern == node)
			ret = true;

		// in case the rule node is a variable
		else if (pattern.getType() == RULEVAR)
			ret = matchRuleVar(pattern, node, config, env);

		// this is possible - since we're peering into e.g., cited-formulas
		// and converting any variable we find into a rule-var
		else if (node.getType() == RULEVAR)
			ret = matchRuleVar(node, pattern, config, env);

		// in case the data node is a variable
		else if (node.isVariable())
			ret = matchNodeVar(node, pattern, env);

		// in case the pattern node is a variable
		else if (pattern.isVariable())
			ret = matchNodeVar(pattern, node, env);

		else {
			if (config.bnodesAsWildcards()) {
				if (pattern.isBlank() || node.isBlank())
					return true;
			}

			// other cases
			switch (pattern.getType()) {
			case ANY:
				ret = true;
				break;

			case CITED_FORMULA:
				ret = matchCitedFormula(pattern, node, config, env);
				break;

			case COLLECTION:
				ret = matchCollection(pattern, node, config, env);
				break;

			default:
				if (Functor.isFunctor(pattern))
					ret = matchFunctor(pattern, node, config, env);

				else if (!config.onlyBindVars())
					ret = pattern.sameValueAs(node);

				break;
			}
		}

//		System.out.println("match? " + ret + " - " + pattern + " <> " + node);
		return ret;
	}

	public static boolean matchRuleVar(Node pattern, Node node, RuleMatchConfig config,
			BindingStack env) {

		boolean alreadyBound = env.isBound(pattern);
		if (config.onlyBindVars() && alreadyBound)
			return true;

		Node node2 = (node.isContainer() ? pullIntoRule(node, env) : node);
		if (alreadyBound) {
			Node binding = env.getBinding(pattern);

			if (binding.getType() == RULEVAR && node.getType() == RULEVAR)
				// doesn't have to be the same variable
				return true;
//				return binding == node;

			return match(binding, node2, config, env);

		} else {
			// avoid binding a variable to itself
			if (pattern != node2)
				env.bind(pattern, node2);
			return true;
		}
	}

	public static Node pullIntoRule(Node node, BindingStack env) {
		// (already pulled into the rule!)
		if (node.includesRuleVars())
			return node;

		N3Rule r = ((N3Rule) env.getRule());
		// may be needed to insert rule vars (into formulas, collections)
		// whenever binding new data into the rule
		Node node2 = r.insertRuleVars(node);
		// when a new node is returned, we're pretty sure it includes rule vars
		if (node2 != node)
			env.expand(r.getNumVars());

		return node2;
	}

	public static boolean matchNodeVar(Node var, Node node, BindingStack env) {
		Node_QuantifiedVariable qv = (Node_QuantifiedVariable) var;

		return (qv.getQuantifier().getQuantifierType() == Quantifiers.UNIVERSAL);
	}

	public static boolean matchCitedFormula(Node pattern, Node node, RuleMatchConfig config,
			BindingStack env) {

		if (!(node instanceof Node_CitedFormula))
			return false;

		Graph g1 = ((Node_CitedFormula) pattern).getContents().getGraph();
		List<Triple> patterns = g1.find().toList();

		Graph g2 = ((Node_CitedFormula) node).getContents().getGraph();
		List<Triple> datas = g2.find().toList();

		// if we're only binding variables we already know that the operands will match
		// exactly - so no need to check for shenanigans here

//		if (config.onlyBindVars())
		return equalToExact(patterns, datas, env, config);
//		else
//			return equalTo(patterns, datas, env, config);
	}

	public static boolean includes(List<Triple> subClauses, int idx, List<Triple> supClauses,
			BindingStack env) {

		return includes(subClauses, idx, supClauses, env, configDefault);
	}

	public static boolean includes(List<Triple> subClauses, int idx, List<Triple> supClauses,
			BindingStack env, RuleMatchConfig config) {

		if (idx == subClauses.size())
			return true;

		Triple subClause = subClauses.get(idx);
		for (Triple supClause : supClauses) {

			env.push();
			boolean match = false;

			boolean bound = match(supClause, subClause, config, env);
			if (bound)
				match = includes(subClauses, idx + 1, supClauses, env, config);

			if (match) {
				env.commit();
				return true;

			} else
				env.unwind();
		}

		return false;
	}

	public static boolean includes(List<Triple> subClauses, int idx, Graph supGraph,
			BindingStack env) {

		if (idx == subClauses.size())
			return true;

		Triple subClause = subClauses.get(idx);

		ExtendedIterator<Triple> it = supGraph.find(subClause);
		while (it.hasNext()) {
			Triple supClause = it.next();

			env.push();
			boolean match = false;

			boolean bound = match(supClause, subClause, configDefault, env);
			if (bound)
				match = includes(subClauses, idx + 1, supGraph, env);

			if (match) {
				env.commit();
				return true;

			} else
				env.unwind();
		}

		return false;
	}

	public static void matchSubGraph(List<Triple> subClauses, int idx, List<Triple> supClauses,
			BindingStack env, List<List<Triple>> subGraphs, List<Triple> curGraph) {

		if (idx == subClauses.size()) {
			subGraphs.add(curGraph);
			return;
		}

		Triple subClause = subClauses.get(idx);
		for (Triple supClause : supClauses) {
			env.push();

			boolean match = match(supClause, subClause, configDefault, env);
			Log.debug(RuleUtil.class,
					"matchSubGraph (manual): " + match + ": " + supClause + " <> " + subClause);
			if (match) {
				List<Triple> newGraph = new ArrayList<>(curGraph);
				newGraph.add(supClause);

				matchSubGraph(subClauses, idx + 1, supClauses, env, subGraphs, newGraph);
			}

			// in any case, we don't want to keep this binding environment
			// (the env is only needed to ensure it's a proper subgraph)
			env.unwind();
		}
	}

	// TODO test
	public static void matchSubGraph(List<Triple> subClauses, int idx, Graph supGraph,
			BindingStack env, List<List<Triple>> subGraphs, List<Triple> curGraph) {

		if (idx == subClauses.size()) {
			subGraphs.add(curGraph);
			return;
		}

		Triple subClause = subClauses.get(idx);
		ExtendedIterator<Triple> it = supGraph.find(subClause);
		while (it.hasNext()) {
			Triple supClause = it.next();

			env.push();

			// already matched values in find() call; only need to bind variables
			match(supClause, subClause, configOnlyBindVars, env);
			Log.info(RuleUtil.class,
					"matchSubGraph (graph.find): " + supClause + " <> " + subClause);

			List<Triple> newGraph = new ArrayList<>(curGraph);
			newGraph.add(supClause);

			matchSubGraph(subClauses, idx + 1, supGraph, env, subGraphs, newGraph);

			// in any case, we don't want to keep this binding environment
			// (the env is only needed to ensure it's a proper subgraph)
			env.unwind();
		}
	}

	public static boolean equalTo(List<Triple> clauses1, List<Triple> clauses2, BindingStack env,
			RuleMatchConfig config) {

		return equalTo(clauses1, 0, clauses2, env, new boolean[clauses2.size()], config);
	}

	public static boolean equalTo(List<Triple> clauses1, int idx, List<Triple> clauses2,
			BindingStack env, boolean[] matched, RuleMatchConfig config) {

		// all clauses in left-operand were matched (& have bindings in env)
		if (idx == clauses1.size()) {

			// but, possible there are un-matched clauses left in right-operand
			List<Triple> remaining2 = new ArrayList<>();
			for (int i = 0; i < matched.length; i++) {
				if (!matched[i])
					remaining2.add(clauses2.get(i));
			}

			// any left? if so, check whether they are included in left-operand
			// (given the current bindings in env)
			if (!remaining2.isEmpty())
				return includes(remaining2, 0, clauses1, env, config);
			else
				return true;
		}

		Triple clause1 = clauses1.get(idx);
		for (int i = 0; i < clauses2.size(); i++) {
			Triple clause2 = clauses2.get(i);

			env.push();
			boolean match = false;

			boolean bound = match(clause2, clause1, config, env);
			if (bound) {
				boolean[] matched2 = new boolean[matched.length];
				System.arraycopy(matched, 0, matched2, 0, matched.length);

				matched2[i] = true;
				match = equalTo(clauses1, idx + 1, clauses2, env, matched2, config);
			}

			if (match) {
				env.commit();
				return true;

			} else
				env.unwind();
		}

		return false;
	}

	public static boolean equalToExact(List<Triple> clauses1, List<Triple> clauses2,
			BindingStack env, RuleMatchConfig config) {

		if (clauses1.size() != clauses2.size())
			return false;

		Iterator<Triple> it1 = clauses1.iterator();
		Iterator<Triple> it2 = clauses2.iterator();

		while (it1.hasNext()) {
			Triple t1 = it1.next();
			Triple t2 = it2.next();

			if (!match(t1, t2, config, env))
				return false;
		}

		return true;
	}

	public static boolean matchCollection(Node pattern, Node node, RuleMatchConfig config,
			BindingStack env) {
		if (!(node instanceof Node_Collection))
			return false;

		Iterator<Node> it1 = ((Node_Collection) pattern).getElements().iterator();
		Iterator<Node> it2 = ((Node_Collection) node).getElements().iterator();

		while (it1.hasNext()) {
			// unequal amount of nodes
			if (!it2.hasNext())
				return false;

			Node t1 = it1.next();
			Node t2 = it2.next();

			if (!match(t1, t2, config, env))
				return false;
		}

		// unequal amount of nodes
		if (it2.hasNext())
			return false;

		return true;
	}

	// (jena-rules)
	public static boolean matchFunctor(Node pattern, Node node, RuleMatchConfig config,
			BindingStack env) {
		if (!Functor.isFunctor(node))
			return false;
		Functor patternF = (Functor) pattern.getLiteralValue();
		Functor nodeF = (Functor) node.getLiteralValue();
		if (!patternF.getName().equals(nodeF.getName()))
			return false;
		Node[] patternArgs = patternF.getArgs();
		Node[] nodeArgs = nodeF.getArgs();
//        if (patternF.isGround()) {
//            return Arrays.equals(patternArgs, nodeArgs);
//        } else {
		if (patternArgs.length != nodeArgs.length)
			return false;
		// Compatible functor shapes so bind an embedded variables in the pattern
		env.push();
		boolean matchOK = true;
		for (int i = 0; i < patternArgs.length; i++) {
			if (!match(patternArgs[i], nodeArgs[i], config, env)) {
				matchOK = false;
				break;
			}
		}
		if (matchOK) {
			env.commit();
			return true;
		} else {
			env.unwind();
			return false;
		}
//        }
	}

	public static TripleSet createTripleSet(TriplePattern clause, BindingStack env, Graph graph,
			Finder continuation) {
		
		BuiltinSet builtinSet = graph.getN3Config().getBuiltinSet();

		Node pred = env.groundShallow(clause.getPredicate());
		if (builtinSet.isBuiltin(pred) && builtinSet.getBuiltin(pred).isStatic()) {
			N3Builtin builtin = builtinSet.getBuiltin(pred);
			return builtin.toTripleSet(clause, graph, continuation);

		} else
			return new PatternTripleSet(clause, graph, continuation);
	}

	public static List<TriplePattern> toTriplePatterns(List<Triple> triples) {
		return triples.stream().map(t -> new TriplePattern(t)).collect(Collectors.toList());
	}

	public static class RuleMatchConfig {

		private boolean onlyBindVars = false;
		private boolean bnodesAsWildcards = false;

		public RuleMatchConfig onlyBindVars(boolean onlyBindVars) {
			this.onlyBindVars = onlyBindVars;
			return this;
		}

		public boolean onlyBindVars() {
			return onlyBindVars;
		}

		public RuleMatchConfig bnodesAsWildcards(boolean bnodesAsWildcards) {
			this.bnodesAsWildcards = bnodesAsWildcards;
			return this;
		}

		public boolean bnodesAsWildcards() {
			return bnodesAsWildcards;
		}
	}
}
