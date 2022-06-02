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

package org.apache.jen3.rdf.model.impl;

import java.util.Iterator;

import org.apache.jen3.enhanced.EnhGraph;
import org.apache.jen3.enhanced.EnhNode;
import org.apache.jen3.enhanced.Implementation;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.NodeTypeRequiredException;
import org.apache.jen3.rdf.model.Resource;

public class CollectionImpl extends ResourceImpl implements Collection {

	final static public Implementation factory = new Implementation() {
		@Override
		public boolean canWrap(Node n, EnhGraph eg) {
			return n.isCollection();
		}

		@Override
		public EnhNode wrap(Node n, EnhGraph eg) {
			if (!n.isCollection())
				throw new NodeTypeRequiredException(n, "Collection");

			return new CollectionImpl(n, eg);
		}
	};

	public CollectionImpl() {
		this((ModelCom) null);
	}

	public CollectionImpl(ModelCom model) {
		super(model);
	}

	public CollectionImpl(Node n, ModelCom m) {
		super(n, m);
	}

	public CollectionImpl(Node n, EnhGraph g) {
		super(n, g);
	}

	@Override
	public Collection asCollection() {
		return this;
	}

	@Override
	public void add(Resource... elements) {
		if (isClosed())
			throw new RuntimeException("Attempting to modify a closed collection.");

		Node_Collection coll = getCollectionNode();
		for (Resource element : elements)			
			coll.addElement(element.asNode());
	}
	
	@Override
	public void remove(Resource... elements) {
		if (isClosed())
			throw new RuntimeException("Attempting to modify a closed collection.");
		
		Node_Collection coll = getCollectionNode();
		for (Resource element : elements)
			coll.removeElement(element.asNode());
	}

	@Override
	public Iterator<Resource> getElements() {
		Iterator<Node> nodeIt = getCollectionNode().getElements().iterator();

		return IteratorFactory.asResIterator(nodeIt, (ModelCom) enhGraph);
	}

	@Override
	public int size() {
		return getCollectionNode().size();
	}

	@Override
	public void close() {
		getCollectionNode().close();
	}

	@Override
	public boolean isClosed() {
		return getCollectionNode().isClosed();
	}

	@Override
	public Collection copy() {
		Collection copy = new CollectionImpl(getCollectionNode().copy(), enhGraph);
		return copy;
	}

	private Node_Collection getCollectionNode() {
		return (Node_Collection) node;
	}
	
	@Override
	public boolean includesVars() {
		return getCollectionNode().includesVars();
	}
}
