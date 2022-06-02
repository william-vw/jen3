/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.graph.n3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jen3.graph.Capabilities;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.GraphEventManager;
import org.apache.jen3.graph.GraphStatisticsHandler;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.TransactionHandler;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.impl.SimpleEventManager;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.impl.N3ModelImpl.N3GraphConfig;
import org.apache.jen3.shared.AddDeniedException;
import org.apache.jen3.shared.DeleteDeniedException;
import org.apache.jen3.shared.PrefixMapping;
import org.apache.jen3.shared.impl.PrefixMappingImpl;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jen3.util.iterator.FilterIterator;

public class N3CitedGraph implements Graph {

	private Scope scope;
	private GraphConfig config;
	private GraphStats stats = new GraphStats();
	private List<Triple> contents = new ArrayList<>();

	protected GraphEventManager gem;

	public N3CitedGraph() {
	}

	public N3CitedGraph(List<Triple> contents) {
		this.contents = contents;
	}

	private N3CitedGraph(Scope scope, GraphStats stats, List<Triple> contents) {
		this.scope = scope;
		this.stats = stats;
		this.contents = contents;
	}

	@Override
	public Capabilities getCapabilities() {
		return capabilities;
	}

	@Override
	public void add(Triple t) throws AddDeniedException {
		checkAdd(t);
		contents.add(t);
	}

	public void checkAdd(Triple t) {
		checkAdd(t.getSubject());
		checkAdd(t.getPredicate());
		checkAdd(t.getObject());
	}

	private void checkAdd(Node n) {
		// TODO ideally this needs to be kept up-to-date
		// i.e., when removing all variables, set this to false

		// only really a problem when creating models programmatically
		// when (incorrectly) true it won't lead to an error, just doing stuff
		// needlessly

		if (n.isVariable() || n.isBlank() || n.includesVars())
			stats.includesVar(n);

		if (n.isRuleVariable())
			stats.includesRuleVar(n);
	}

	@Override
	public void clear() {
		contents.clear();
	}

	@Override
	public boolean isEmpty() {
		return contents.isEmpty();
	}

	@Override
	public int size() {
		return contents.size();
	}

	public Iterator<Triple> getContents() {
		return contents.iterator();
	}

	@Override
	public ExtendedIterator<Triple> find(Triple m) {
		return new FilterIterator<>(t -> m.matches(t), contents.iterator());
	}

	@Override
	public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
		return new FilterIterator<>(t -> t.matchedBy(s, p, o), contents.iterator());
	}

	@Override
	public PrefixMapping getPrefixMapping() {
		return prefixMap;
	}

	@Override
	public GraphStats getStats() {
		return stats;
	}

	@Override
	public boolean hasScope() {
		return scope != null;
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return contents.toString();
	}

	// does a shallow copy

	public N3CitedGraph copy() {
		return new N3CitedGraph(scope, stats, new ArrayList<>(contents));
	}

	// will return "1" in case graph is empty

	@Override
	public int hashCode() {
		int i = 1;
		// order doesn't matter
		for (Triple t : contents)
//			i = 31 * i + t.hashCode();
			i += t.hashCode();

		return i;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof N3CitedGraph))
			return false;

		N3CitedGraph g = (N3CitedGraph) o;

		if (g.size() != size())
			return false;

		Iterator<Triple> it = contents.iterator();
		l0: while (it.hasNext()) {
			Triple t = it.next();

			Iterator<Triple> it2 = g.contents.iterator();
			while (it2.hasNext()) {
				Triple t2 = it2.next();

				if (t.equals(t2))
					continue l0;
			}

			// no match found for t
			return false;
		}

		return true;
	}

	@Override
	public boolean dependsOn(Graph other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TransactionHandler getTransactionHandler() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Answer the event manager for this graph; allocate a new one if required.
	 * Subclasses may override if they have a more specialised event handler. The
	 * default is a SimpleEventManager.
	 */
	@Override
	public GraphEventManager getEventManager() {
		if (gem == null)
			gem = new SimpleEventManager();
		return gem;
	}

	@Override
	public GraphStatisticsHandler getStatisticsHandler() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Triple t) throws DeleteDeniedException {
		contents.remove(t);
	}

	@Override
	public boolean isIsomorphicWith(Graph g) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Node s, Node p, Node o) {
		return contents.contains(new Triple(s, p, o));
	}

	@Override
	public boolean contains(Triple t) {
		return contents.contains(t);
	}

	@Override
	public void remove(Node s, Node p, Node o) {
		contents.remove(new Triple(s, p, o));
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public void setConfig(GraphConfig config) {
		this.config = config;
	}

	@Override
	public GraphConfig getConfig() {
		return config;
	}

	@Override
	public N3GraphConfig getN3Config() {
		return (N3GraphConfig) getConfig();
	}

	private PrefixMapping prefixMap = new PrefixMappingImpl();

	private static final Capabilities capabilities = new Capabilities() {

		@Override
		public boolean sizeAccurate() {
			return true;
		}

		@Override
		public boolean iteratorRemoveAllowed() {
			return false;
		}

		@Override
		public boolean handlesLiteralTyping() {
			return false;
		}

		@Override
		public boolean findContractSafe() {
			return false;
		}

		@Override
		public boolean deleteAllowed(boolean everyTriple) {
			return false;
		}

		@Override
		public boolean deleteAllowed() {
			return false;
		}

		@Override
		public boolean canBeEmpty() {
			return true;
		}

		@Override
		public boolean addAllowed(boolean everyTriple) {
			return true;
		}

		@Override
		public boolean addAllowed() {
			return true;
		}
	};
}
