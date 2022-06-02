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

package org.apache.jen3.enhanced;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jena.atlas.lib.Cache;
import org.apache.jena.atlas.lib.CacheFactory;

/**
 * {@code EnhGraph} wraps a {@link Graph plain graph} and contains
 * {@link EnhNode enhanced nodes} that wrap the plain nodes of the plain graph.
 * All the enhanced nodes in the enhanced graph share the same polymorphic
 * personality.
 */

public class EnhGraph {
	// Instance variables
	/** The graph that this enhanced graph is wrapping */
	protected Graph graph;

	/** Cache of enhanced nodes that have been created */
	// 1000 seems to be a "about the right size" by experimentation.
	protected Cache<Node, RDFObject> cache = CacheFactory.createCache(1000);

	/** The unique personality that is bound to this polymorphic instance */
	protected Personality<RDFObject> personality;

	// Constructors
	public EnhGraph() {
		super();
	}

	public EnhGraph(Personality<RDFObject> p) {
		super();
		personality = p;
	}

	/**
	 * Construct an enhanced graph from the given underlying graph, and a factory
	 * for generating enhanced nodes.
	 * 
	 * @param g The underlying plain graph, may be null to defer binding to a given
	 *          graph until later.
	 * @param p The personality factory, that maps types to realisations
	 */
	public EnhGraph(Graph g, Personality<RDFObject> p) {
		super();
		graph = g;
		personality = p;
	}

	/**
	 * Answer the normal graph that this enhanced graph is wrapping.
	 * 
	 * @return A graph
	 */
	public Graph asGraph() {
		return graph;
	}

	/**
	 * Hashcode for an enhanced graph is delegated to the underlying graph.
	 * 
	 * @return The hashcode as an int
	 */
	@Override
	final public int hashCode() {
		return graph.hashCode();
	}

	/**
	 * An enhanced graph is equal to another graph g iff the underlying graphs are
	 * equal. This is deemed to be a complete and correct interpretation of enhanced
	 * graph equality, which is why this method has been marked final.
	 * <p>
	 * Note that this equality test does not look for correspondence between the
	 * structures in the two graphs. To test whether another graph has the same
	 * nodes and edges as this one, use {@link #isIsomorphicWith}.
	 * </p>
	 * 
	 * @param o An object to test for equality with this node
	 * @return True if o is equal to this node.
	 * @see #isIsomorphicWith
	 */
	@Override
	final public boolean equals(Object o) {
		return this == o || o instanceof EnhGraph && graph.equals(((EnhGraph) o).asGraph());
	}

	/**
	 * Answer true if the given enhanced graph contains the same nodes and edges as
	 * this graph. The default implementation delegates this to the underlying graph
	 * objects.
	 * 
	 * @param eg A graph to test
	 * @return True if eg is a graph with the same structure as this.
	 */
	final public boolean isIsomorphicWith(EnhGraph eg) {
		return graph.isIsomorphicWith(eg.graph);
	}

	public <X extends RDFObject> X create(Node n, Class<X> interf) {
		return personality.newInstance(interf, n, this);
	}

	public RDFObject getCached(Node n) {
		return cache.getIfPresent(n);
	}

	public <X extends RDFObject> X createCached(Node n, Class<X> interf) {
		X constructed = create(n, interf);
		cache.put(n, constructed);
		return constructed;
	}

	/**
	 * Answer an enhanced node that wraps the given node and conforms to the given
	 * interface type.
	 * 
	 * @param n      A node (assumed to be in this graph)
	 * @param interf A type denoting the enhanced facet desired
	 * @return An enhanced node
	 */
	public <X extends RDFObject> X getNodeAs(Node n, Class<X> interf) {
		// We use a cache to avoid reconstructing the same Node too many times.
		EnhNode eh = (EnhNode) cache.getIfPresent(n);
		if (eh == null) {
			// not in the cache, so build a new one
			X constructed = personality.newInstance(interf, n, this);
			cache.put(n, constructed);
			return constructed;
		} else
			return eh.viewAs(interf);
	}

	/**
	 * Set the cache controller object for this graph
	 * 
	 * @param cc The cache controller
	 */
	public void setNodeCache(Cache<Node, RDFObject> cc) {
		cache = cc;
	}

	/**
	 * Answer the personality object bound to this polymorphic instance
	 * 
	 * @return The personality object
	 */
	protected Personality<RDFObject> getPersonality() {
		return personality;
	}
}
