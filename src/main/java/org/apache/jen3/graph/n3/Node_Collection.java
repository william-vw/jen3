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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeTypes;
import org.apache.jen3.graph.NodeVisitor;
import org.apache.jen3.graph.Node_Compound;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.shared.PrefixMapping;

/**
 * Represents an N3 collection.
 * 
 * 
 * @author wvw
 *
 */

public class Node_Collection extends Node_Compound {

	private List<Node> elements;

	private boolean closed = false;

	// set when the collection is closed (close method)
	// and does not include variables
	// opening and closing is managed by CollectionImpl class
	private int hashCode = -2;

	// TODO ideally this needs to be kept up-to-date
	// i.e., when removing all variables, set this to false
	private boolean includesVars = false;

	// TODO idem
	private boolean includesRuleVars = false;

	public Node_Collection() {
		super(null);

		this.elements = new ArrayList<>();
	}

	public Node_Collection(Collection<Node> elements) {
		super(null);

		this.elements = new ArrayList<>();
		elements.stream().forEach(n -> addElement(n));
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.COLLECTION;
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public boolean isResource() {
		return true;
	}

	public void addElement(Node n) {
		elements.add(n);

		if (n.isVariable() || n.isBlank() || n.includesVars())
			this.includesVars = true;

		if (n.isRuleVariable())
			this.includesRuleVars = true;
	}

	public void removeElement(Node element) {
		elements.remove(element);
	}

	public Node removeAt(int idx) {
		return elements.remove(idx);
	}

	public Node getElement(int idx) {
		return elements.get(idx);
	}

	public boolean hasElement(Node element) {
		return elements.contains(element);
	}

	public void setElement(int idx, Node element) {
		elements.set(idx, element);
	}

	public List<Node> getElements() {
		return elements;
	}

	public int indexOf(Node n) {
		return elements.indexOf(n);
	}

	public int size() {
		return elements.size();
	}

	public void close() {
		closed = true;

		if (isHashable())
			hashCode = elements.hashCode();
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		return v.visitCollection(this, data);
	}

	public Node_Collection copy() {
		return new Node_Collection(new ArrayList<>(elements));
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean includesVars() {
		return includesVars;
	}

	@Override
	public boolean includesRuleVars() {
		return includesRuleVars;
	}

	@Override
	public List<Node_RuleVariable> getRuleVars() {
		return elements.stream().filter(n -> n.isRuleVariable()).map(n -> (Node_RuleVariable) n)
				.collect(Collectors.toList());
	}

	@Override
	public boolean isHashable() {
		return !includesVars;
	}

	@Override
	public int hashCode() {
		if (!closed)
			close();
		// throw new RuntimeException("Attempting to get hashcode from a non-closed
		// collection node.");

		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Node_Collection))
			return super.equals(o);

		if (!closed)
			close();
		// throw new RuntimeException("Attempting to check equality with a non-closed
		// collection node.");

		Node_Collection c = (Node_Collection) o;

		// was possible to generate a hash-code
		if (hashCode >= 0) {
			// hashCode is pre-calculated, so quickly rule out non-equals that way
			if (hashCode() != c.hashCode())
				return false;
		}

		if (c.size() != size())
			return false;

		Iterator<Node> it = c.getElements().iterator();
		Iterator<Node> it2 = elements.iterator();
		while (it.hasNext()) {
			Node el = it.next();
			Node el2 = it2.next();

			if (!el.equals(el2))
				return false;
		}

		return true;
	}

	@Override
	public String toString(PrefixMapping pm, boolean quoting) {
		return "( " + elements.stream().map(e -> e.toString()).collect(Collectors.joining(", "))
				+ " )";
	}
}