package org.apache.jen3.reasoner.rulesys.impl;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_Compound;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.reasoner.rulesys.RuleContext;
import org.apache.jen3.util.OneToManyMap;

public class BindingStack_Cached extends BindingStack {

	private CompoundBindingCache cache;

	public BindingStack_Cached(RuleContext ctx) {
		super(ctx);
	}

	protected BindingStack_Cached(Node[] environment) {
		super(environment);
	}

	public BindingStack_Cached() {
		super();
	}

	@Override
	protected void init() {
		cache = new CompoundBindingCache();
		instVisitor = new InstantiateTriplePatternVisitor_Cached();
	}

	@Override
	public boolean bind(int i, Node value) {
		Node node = environment[i];
		if (node == null) {
			environment[i] = value;
			cache.onNewBinding(i);
			return true;

		} else {
			return node.sameValueAs(value);
		}
	}

	public class InstantiateTriplePatternVisitor_Cached extends InstantiateTriplePatternVisitor {

		@Override
		public Node visitCitedFormula(Node_CitedFormula it, Object data) {
			Node_Compound ret = null;

			if (it.includesRuleVars()) {
				Node_Compound cached = cache.check(it);

				if (cached != null) {
					ret = cached;

				} else {
					nrNewGraphs++;

					Scope priorScope = curScope;
					curScope = it.getScope();

					ret = (Node_CitedFormula) super.visitCitedFormula(it, data);
					cache.cache(it, ret);

					curScope = priorScope;
				}

			} else
				ret = it;

			return ret;
		}

		@Override
		public Node visitCollection(Node_Collection it, Object data) {
			InstantiateData inst = (InstantiateData) data;

			Node_Compound ret = null;
			if (it.includesVars()) {
				Node_Compound cached = cache.check(it);

				if (cached != null) {
					ret = cached;

				} else {
					nrNewColls++;

					ret = new Node_Collection(it.getElements().stream().map(n -> visitNode(n, inst))
							.collect(Collectors.toList()));
					cache.cache(it, ret);
				}

			} else
				ret = it;

			return ret;
		}
	}

	private class CompoundBindingCache {

		private OneToManyMap<Integer, Node_Compound> varToCmpdCache = new OneToManyMap<>();

		// caching part

		public void onNewBinding(int i) {
			if (varToCmpdCache.containsKey(i))
				varToCmpdCache.getAll(i).forEachRemaining(cmpd -> cmpd.resetCached());
		}

		public Node_Compound check(Node_Compound it) {
			if (it.includesRuleVars()) {
				if (!isBeingCached(it)) {
					// not yet setup for caching, so let's do that now
					setupForCaching(it);
					return null;
				}
			}

			return it.getCached();
		}

		private boolean isBeingCached(Node_Compound it) {
			int idx = it.getRuleVars().get(0).getIndex();

			Iterator<Node_Compound> values = varToCmpdCache.getAll(idx);
			while (values.hasNext()) {
				if (values.next() == it)
					return true;
			}

			return false;
		}

		private void setupForCaching(Node_Compound it) {
			it.getRuleVars().forEach(var -> varToCmpdCache.put(var.getIndex(), it));
		}

		public void cache(Node_Compound or, Node_Compound cached) {
			or.setCached(cached);
		}
	}
}
