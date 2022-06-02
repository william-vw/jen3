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

import org.apache.jen3.enhanced.EnhGraph;
import org.apache.jen3.enhanced.EnhNode;
import org.apache.jen3.enhanced.Implementation;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.rdf.model.NodeTypeRequiredException;
import org.apache.jen3.rdf.model.QuantifiedVariable;
import org.apache.jen3.rdf.model.Quantifier;

public class QuantifiedVariableImpl extends ResourceImpl implements QuantifiedVariable {

	final static public Implementation factory = new Implementation() {
		@Override
		public boolean canWrap(Node n, EnhGraph eg) {
			return n.isQuantifiedVariable();
		}

		@Override
		public EnhNode wrap(Node n, EnhGraph eg) {
			if (!n.isQuantifiedVariable())
				throw new NodeTypeRequiredException(n, "QuantifiedVariable");

			return new QuantifiedVariableImpl(n, eg);
		}
	};

	public QuantifiedVariableImpl() {
		this((ModelCom) null);
	}

	public QuantifiedVariableImpl(ModelCom model) {
		super(model);
	}

	public QuantifiedVariableImpl(Node n, ModelCom m) {
		super(n, m);
	}

	public QuantifiedVariableImpl(Node n, EnhGraph g) {
		super(n, g);
	}

	@Override
	public QuantifiedVariable asQuantifiedVariable() {
		return this;
	}

	@Override
	public Quantifier getQuantifier() {
		return new QuantifierImpl(((Node_QuantifiedVariable) node).getQuantifier(), getModelCom());
	}

	/**
	 * Answer the graph node that this enhanced node wraps.
	 * 
	 * This overridden method will set some of the internal state of the node.
	 * 
	 * @return A plain node
	 */
	@Override
	public Node asNode() {
		return node;
	}

	@Override
	public String getName() {
		return ((Node_QuantifiedVariable) node).getName();
	}
}
