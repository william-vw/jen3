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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeTypes;
import org.apache.jen3.graph.NodeVisitor;

/**
 * Represents an N3 quantifier.
 * 
 * 
 * @author wvw
 *
 */

public class Node_Quantifier extends Node {

	private Quantifiers quantifier;
	private Set<Node_QuantifiedVariable> variables = new HashSet<>();

	public Node_Quantifier(Quantifiers quantifier) {
		super(null);

		this.quantifier = quantifier;
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.QUANTIFIER;
	}

	@Override
	public boolean isConcrete() {
		return false;
	}

	@Override
	public boolean isQuantifier() {
		return true;
	}

	@Override
	public boolean isResource() {
		return false;
	}

	public Iterator<Node_QuantifiedVariable> getVariables() {
		return variables.iterator();
	}

	public boolean isExistential() {
		return quantifier == Quantifiers.EXISTENTIAL;
	}

	public boolean isUniversal() {
		return quantifier == Quantifiers.UNIVERSAL;
	}

	public Quantifiers getQuantifierType() {
		return quantifier;
	}

	// variables should add themselves via this method
	public void add(Node_QuantifiedVariable var) {
		variables.add(var);
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		return v.visitQuantifier(this, data);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Node_Quantifier))
			return false;

		Node_Quantifier q = (Node_Quantifier) o;
		return q.quantifier == quantifier;
	}

	@Override
	public String toString() {
		return // scope + " " +
		quantifier + ": " + variables.stream().map(v -> v.getName()).collect(Collectors.joining(", "));
	}
}