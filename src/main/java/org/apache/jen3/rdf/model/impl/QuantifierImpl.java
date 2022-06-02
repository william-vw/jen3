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
import org.apache.jen3.graph.n3.Node_Quantifier;
import org.apache.jen3.graph.n3.Quantifiers;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.NodeTypeRequiredException;
import org.apache.jen3.rdf.model.QuantifiedVariable;
import org.apache.jen3.rdf.model.Quantifier;
import org.apache.jen3.rdf.model.QuickVariable;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.RDFVisitor;
import org.apache.jen3.rdf.model.Resource;

/**
 * (based on LiteralImpl)
 * 
 * @author wvw
 *
 */

public class QuantifierImpl extends EnhNode implements Quantifier {

	final static public Implementation factory = new Implementation() {
		@Override
		public boolean canWrap(Node n, EnhGraph eg) {
			return n.isQuantifier();
		}

		@Override
		public EnhNode wrap(Node n, EnhGraph eg) {
			if (!n.isQuantifier())
				throw new NodeTypeRequiredException(n, "Quantifier");

			return new QuantifierImpl(n, eg);
		}
	};

	public QuantifierImpl(Node n, ModelCom model) {
		super(n, model);
	}

	public QuantifierImpl(Node n, EnhGraph g) {
		super(n, g);
	}

	@Override
	public Quantifiers getQuantifierType() {
		return getQuantifierNode().getQuantifierType();
	}

	@Override
	public Iterator<Resource> getVariables() {
		return IteratorFactory.asResIterator((Iterator) getQuantifierNode().getVariables(), (ModelCom) enhGraph);
	}

	@Override
	public Model getModel() {
		return (ModelCom) getGraph();
	}

	@Override
	public RDFObject inModel(Model m) {
		return getModel() == m ? this : m.getRDFObject(asNode());
	}

	@Override
	public Object visitWith(RDFVisitor rv) {
		// TODO ...
		return null;
	}

	@Override
	public Resource asResource() {
		throw new NodeTypeRequiredException(asNode(), "Resource");
	}

	@Override
	public Literal asLiteral() {
		throw new NodeTypeRequiredException(asNode(), "Literal");
	}

	@Override
	public CitedFormula asCitedFormula() {
		throw new NodeTypeRequiredException(this, "Formula");
	}

	@Override
	public QuantifiedVariable asQuantifiedVariable() {
		throw new NodeTypeRequiredException(this, "QuantifiedVariable");
	}

	@Override
	public QuickVariable asQuickVariable() {
		throw new NodeTypeRequiredException(this, "QuickVariable");
	}

	@Override
	public Collection asCollection() {
		throw new NodeTypeRequiredException(this, "Collection");
	}

	@Override
	public Quantifier asQuantifier() {
		return this;
	}

	private Node_Quantifier getQuantifierNode() {
		return (Node_Quantifier) node;
	}

	@Override
	public String toString() {
		return node.toString();
	}
}