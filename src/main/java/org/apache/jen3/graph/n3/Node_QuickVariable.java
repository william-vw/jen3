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

public class Node_QuickVariable extends Node_QuantifiedVariable {

	public Node_QuickVariable(String name, Node_Quantifier quantifier) {
		super(name, quantifier);
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.QUICK_VAR;
	}

	public boolean isQuickVariable() {
		return true;
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		return v.visitQuickVariable(this, data);
	}

	@Override
	public boolean equals(Object o) {		
		if (o instanceof Node_QuickVariable)
			return label.equals(((Node_QuickVariable) o).label);

//		else if (o instanceof Node_RuleVariable)
//			return ((VariableName) label).getName().equals(((Node_RuleVariable) o).getName());

		else
			return super.equals(o);
	}

	@Override
	public String toString() {
		return "?" + ((VariableName) label).getName();
	}
}
