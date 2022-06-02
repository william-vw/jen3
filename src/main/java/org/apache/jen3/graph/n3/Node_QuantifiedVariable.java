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

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeTypes;
import org.apache.jen3.graph.NodeVisitor;
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.shared.PrefixMapping;

/**
 * Represents an N3 quantified-variable.
 * 
 * @author wvw
 *
 */

public class Node_QuantifiedVariable extends Node_Variable {

	protected Node_Quantifier quantifier;

	public Node_QuantifiedVariable(String name) {
		super(name);
	}

	public Node_QuantifiedVariable(String name, Node_Quantifier quantifier) {
		super(name);

		setQuantifier(quantifier);
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.QUANT_VAR;
	}

	@Override
	public boolean isQuantifiedVariable() {
		return true;
	}

	@Override
	public boolean isResource() {
		return true;
	}

	public void setQuantifier(Node_Quantifier quantifier) {
		this.quantifier = quantifier;
		quantifier.add(this);
	}

	public Node_Quantifier getQuantifier() {
		return quantifier;
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		return v.visitQuantifiedVariable(this, data);
	}

	// TODO could override hashCode here to make hashcode different for same-named
	// univ vs. exist variables ;
	// but, this makes it a lot easier to compare with node_rulevars

	@Override
	public boolean equals(Object o) {
		if (o instanceof Node_QuantifiedVariable) {
			Node_QuantifiedVariable qv = (Node_QuantifiedVariable) o;

			if (qv.label.equals(label) && qv.quantifier.equals(quantifier))
				return true;
			else
				return super.equalsNamed(o);
		}
//		else if (o instanceof Node_RuleVariable) {
//			if (quantifier.getQuantifierType() == QuantifierTypes.UNIVERSAL && quantifier.getScope().isOuter()) {
//
//				Node_RuleVariable rv = (Node_RuleVariable) o;
//				return ((VariableName) label).getName().equals(rv.getName());
//			}
//		}

		return false;
	}

	@Override
	public boolean isDataVar() {
		return (quantifier.getQuantifierType() == Quantifiers.UNIVERSAL);
	}

	@Override
	public String toString(PrefixMapping pm, boolean quoting) {
		return toString();
	}

	@Override
	public String toString() {
		return (quantifier.isExistential() ? "∃" : "∀") + ((VariableName) label).getName();
	}
}