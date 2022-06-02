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

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeTypes;
import org.apache.jen3.graph.NodeVisitor;
import org.apache.jen3.graph.Node_Compound;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.graph.n3.scope.Scope.Scopes;
import org.apache.jen3.graph.n3.scope.ScopedObject;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.shared.PrefixMapping;

/**
 * Represents an N3 cited formula.
 * 
 * 
 * @author wvw
 *
 */

public class Node_CitedFormula extends Node_Compound implements ScopedObject {

	private boolean closed = false;

	// set when the formula is closed (close method),
	// and the formula was hashable (see isHashable)
	// else, the formulas will be hashed under this key
	private int hashCode = -1;

	private N3Model model;

	public Node_CitedFormula(Scope scope) {
		this(scope, N3ModelSpec.get(N3_MEM), false);
	}

	public Node_CitedFormula(Scope scope, N3Model model) {
		this(scope, model, true);
	}

	public Node_CitedFormula(Scope scope, N3ModelSpec spec) {
		this(scope, spec, false);
	}

	private Node_CitedFormula(Scope scope, N3ModelSpec spec, boolean immutable) {
		this(scope, ModelFactory.createN3Model(spec, new N3CitedGraph()), immutable);
	}

	private Node_CitedFormula(Scope scope, N3Model model, boolean immutable) {
		super(null);

		init(scope, model, immutable);
	}

	private void init(Scope scope, N3Model model, boolean immutable) {
		N3ModelSpec spec = model.getSpec();

		// setup spec from parent spec, if any

		if (scope.hasParent()) {
			if (scope.getParent().hasScoped()) {
				N3Model m = (N3Model) scope.getParent().getScoped();
				spec.from(m.getSpec());
			}
		}

		this.model = model;
		if (scope != null)
			scope.attach(model);
		if (immutable)
			model.setImmutable();
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.CITED_FORMULA;
	}

	@Override
	public boolean isCitedFormula() {
		return true;
	}

	@Override
	public boolean isResource() {
		return true;
	}

	@Override
	public boolean hasScope() {
		return model.getScope() != null;
	}

	@Override
	public void setScope(Scope scope) {
		scope.attach(model);
	}

	@Override
	public Scope getScope() {
		return model.getScope();
	}

	public N3Model getContents() {
		return model;
	}

	public long size() {
		return model.size();
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		return v.visitCitedFormula(this, data);
	}

	public Node_CitedFormula copy() {
		N3Model copy = model.copy();
		return new Node_CitedFormula(getScope().leveled(Scopes.GRAPH), copy, false);
	}

	public N3Model open() {
		if (model.isImmutable())
			throw new RuntimeException("Attempting to populate an immutable N3 model.");

		return model;
	}

	public void close() {
		closed = true;

		model.setImmutable();
		if (isHashable())
			hashCode = model.hashCode();
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean includesVars() {
		return model.getGraph().getStats().includesVars();
	}

	@Override
	public boolean includesRuleVars() {
		return model.getGraph().getStats().includesRuleVars();
	}
	
	@Override
	public List<Node_RuleVariable> getRuleVars() {
		return model.getGraph().getStats().getRuleVars();
	}

	@Override
	public boolean isHashable() {
		return !includesVars();
//		return true;
	}

	@Override
	public int hashCode() {
		if (!closed)
			close();

		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Node_CitedFormula))
			return super.equals(o);

		if (!closed)
			close();
		// throw new RuntimeException("Attempting to check equality with a non-closed
		// formula node.");

		Node_CitedFormula n = (Node_CitedFormula) o;

		// was possible to generate a hash-code
		if (hashCode >= 0) {
			// hashCode is pre-calculated, so quickly rule out non-equals that way
			if (hashCode() != n.hashCode())
				return false;
		}

		boolean ret = (model.getGraph().equals(n.getContents().getGraph()));
		return ret;
	}

	@Override
	public String toString(PrefixMapping pm, boolean quoting) {
		return model.toString();
	}
}