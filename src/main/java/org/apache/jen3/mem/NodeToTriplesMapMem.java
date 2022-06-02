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

package org.apache.jen3.mem;

import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.Triple.Field;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.graph.n3.Quantifiers;
import org.apache.jen3.shared.JenaException;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jen3.util.iterator.NullIterator;

public class NodeToTriplesMapMem extends NodeToTriplesMapBase {
	public NodeToTriplesMapMem(Field indexField, Field f2, Field f3) {
		super(indexField, f2, f3);
	}

	/**
	 * Add <code>t</code> to this NTM; the node <code>o</code> <i>must</i> be the
	 * index node of the triple. Answer <code>true</code> iff the triple was not
	 * previously in the set, ie, it really truly has been added.
	 */
	@Override
	public boolean add(Triple t) {
		Object o = getIndexField(t);

		if ((o instanceof Node) && ((Node) o).isVariable()) {
			Node_QuantifiedVariable qv = (Node_QuantifiedVariable) o;

			if (qv.getQuantifier().getQuantifierType() == Quantifiers.UNIVERSAL)
				indexedVars = true;
		}

		// Feb 2016 : no measurable difference.
		// TripleBunch s = bunchMap.getOrSet(o, (k)->new ArrayBunch()) ;

		TripleBunch s = bunchMap.get(o);

		if (s == null)
			bunchMap.put(o, s = new ArrayBunch());
		if (s.contains(t))
			return false;
		else {
			if (s.size() == 9 && s instanceof ArrayBunch)
				bunchMap.put(o, s = new HashedTripleBunch(s));
			s.add(t);
			size += 1;
			return true;
		}
	}

	/**
	 * Remove <code>t</code> from this NTM. Answer <code>true</code> iff the triple
	 * was previously in the set, ie, it really truly has been removed.
	 */
	@Override
	public boolean remove(Triple t) {
		Object o = getIndexField(t);
		TripleBunch s = bunchMap.get(o);
		if (s == null || !s.contains(t))
			return false;
		else {
			s.remove(t);
			size -= 1;
			if (s.size() == 0)
				bunchMap.remove(o);
			return true;
		}
	}

	/**
	 * Answer an iterator over all the triples in this NTM which have index node
	 * <code>o</code>.
	 */
	@Override
	public Iterator<Triple> iterator(Object o, HashCommon.NotifyEmpty container) {
		// System.err.println( ">> BOINK" ); // if (true) throw new JenaException(
		// "BOINK" );
		TripleBunch s = bunchMap.get(o);
		return s == null ? NullIterator.<Triple>instance() : s.iterator(container);
	}

	public class NotifyMe implements HashCommon.NotifyEmpty {
		protected final Object key;

		public NotifyMe(Object key) {
			this.key = key;
		}

		// TODO fix the way this interacts (badly) with iteration and CMEs.
		@Override
		public void emptied() {
			if (false)
				throw new JenaException("BOOM");
			/* System.err.println( ">> OOPS" ); */ bunchMap.remove(key);
		}
	}

	/**
	 * Answer true iff this NTM contains the concrete triple <code>t</code>.
	 */
	@Override
	public boolean contains(Triple t) {
		TripleBunch s = bunchMap.get(getIndexField(t));
		return s == null ? false : s.contains(t);
	}

	@Override
	public boolean containsBySameValueAs(Triple t) {
		TripleBunch s = bunchMap.get(getIndexField(t));
		return s == null ? false : s.containsBySameValueAs(t);
	}

	/**
	 * Answer an iterator over all the triples in this NTM which match
	 * <code>pattern</code>. The index field of this NTM is guaranteed concrete in
	 * the pattern.
	 */
	@Override
	public ExtendedIterator<Triple> iterator(Node index, Node n2, Node n3, boolean matchAbsolute) {
		Object indexValue = index.getIndexingValue();
		TripleBunch res = bunchMap.get(indexValue);

//       System.err.println( ">> ntmf::iterator: " + (s == null ? (Object) "None" : s.getClass()) );

		final Predicate<Triple> otherFilter = f2.filterOn(n2, matchAbsolute)
				.and(f3.filterOn(n3, matchAbsolute));

		ExtendedIterator<Triple> vars = null;

		// in case any universal variables were indexed in this s/p/o map
		// (& not matching variables on name; matchAbsolute):
		// need to include all triples with var at this s/p/o position

		// TODO could optimize this by keeping pointers to all triples
		// with universal var - this code requires iterating over all triples

		if (indexedVars && !matchAbsolute)
			vars = iterateAll().filterKeep(indexField.filterWildcardVar().and(otherFilter));
		else
			vars = NullIterator.<Triple>instance();

		if (res == null)
			return NullIterator.<Triple>instance().andThen(vars);

		// (already filtered on indexField via retrieval from bunchMap)
		return (res.iterator(new NotifyMe(indexValue)).filterKeep(otherFilter)).andThen(vars);
	}

	protected TripleBunch get(Object index) {
		return bunchMap.get(index);
	}

	/**
	 * Answer an iterator over all the triples that are indexed by the item
	 * <code>y</code>. Note that <code>y</code> need not be a Node (because of
	 * indexing values).
	 */
	@Override
	public Iterator<Triple> iteratorForIndexed(Object y) {
		return get(y).iterator();
	}
}
