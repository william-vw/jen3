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
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.NodeTypeRequiredException;
import org.apache.jen3.rdf.model.StmtIterator;

public class CitedFormulaImpl extends ResourceImpl implements CitedFormula {

	final static public Implementation factory = new Implementation() {
		@Override
		public boolean canWrap(Node n, EnhGraph eg) {
			return n.isCitedFormula();
		}

		@Override
		public EnhNode wrap(Node n, EnhGraph eg) {
			if (!n.isCitedFormula())
				throw new NodeTypeRequiredException(n, "CitedFormula");

			return new CitedFormulaImpl(n, eg);
		}
	};

	public CitedFormulaImpl() {
		this((ModelCom) null);
	}

	public CitedFormulaImpl(ModelCom model) {
		super(model);
	}

	public CitedFormulaImpl(Node n, ModelCom m) {
		super(n, m);
	}

	public CitedFormulaImpl(Node n, EnhGraph g) {
		super(n, g);
	}

	@Override
	public CitedFormula asCitedFormula() {
		return this;
	}

	@Override
	public N3Model open() {
		return getFormulaNode().open();
	}

	@Override
	public void close() {
		getFormulaNode().close();
	}

	@Override
	public boolean isClosed() {
		return getContents().isImmutable();
	}

	@Override
	public StmtIterator listStatements() {
		return getContents().listStatements();
	}

	@Override
	public N3Model getImmutableContents() {
		N3Model model = getContents();
		if (!model.isImmutable())
			throw new RuntimeException("Populate the CitedFormula first using open() and close() methods.");

		return model;
	}

	@Override
	public CitedFormula copy() {
		CitedFormula copy = new CitedFormulaImpl(getFormulaNode().copy(), enhGraph);
		return copy;
	}

	@Override
	public boolean hasScope() {
		return getFormulaNode().hasScope();
	}

	@Override
	public void setScope(Scope scope) {
		getFormulaNode().setScope(scope);
	}

	@Override
	public Scope getScope() {
		return getFormulaNode().getScope();
	}

	private N3Model getContents() {
		return getFormulaNode().getContents();
	}

	private Node_CitedFormula getFormulaNode() {
		return (Node_CitedFormula) node;
	}
	
	@Override
	public boolean includesVars() {
		return getFormulaNode().includesVars();
	}
}