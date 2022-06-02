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


package org.apache.jen3.graph;

import org.apache.jen3.graph.n3.Node_QuantifiedVariable;

/**
 * This class does nothing more than finalize the {@link Node_Concrete} class.
 * It is used to search maps based on the labels of nodes.
 * 
 * We cannot make {@link Node_QuantifiedVariable} or {@link Node_URI} subclasses
 * because the equals method should separately consider equivalency between
 * {@link Node_Named} individuals on the one hand, and {@link Node_Concrete} or
 * {@link Node_QuantifiedVariable} subclass individuals on the other.
 * 
 * E.g., individuals of {@link Node_QuantifiedVariable} should not be equal just
 * because they have the same name - they should also have the same quantifier.
 * 
 * 
 * @author wvw
 *
 */

public class Node_Named extends Node_Concrete {

	public Node_Named(Object label) {
		super(label);
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.NAME;
	}

	@Override
	public boolean equals(Object o) {
		return ((o instanceof Node_Concrete && label.equals(((Node_Concrete) o).getLabel()))
				|| (o instanceof Node_QuantifiedVariable && label.equals(((Node_QuantifiedVariable) o).getName())));
	}
}