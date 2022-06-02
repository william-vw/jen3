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

import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.shared.PrefixMapping;

/**
 * RDF blank nodes, ie nodes with identity but without URIs.
 */

public class Node_Blank extends Node_Concrete {

	private Scope scope;

	/** @deprecated Use {@link Node_Blank#BlankNodeId} */
	@Deprecated
//	/* package */ Node_Blank(Object id) {
//		super(id);
//	}

	/* package */ Node_Blank(BlankNodeId id, Scope scope) {
		super(id);

		this.scope = scope;
	}

	@Override
	public NodeTypes getType() {
		return NodeTypes.BLANK;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public boolean isBlank() {
		return true;
	}

	@Override
	public boolean isResource() {
		return true;
	}

	@Override
	public BlankNodeId getBlankNodeId() {
		return (BlankNodeId) label;
	}

	@Override
	public Node visitWith(NodeVisitor v, Object data) {
		return v.visitBlank(this, (BlankNodeId) label, data);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other instanceof Node_Blank && label.equals(((Node_Blank) other).label))
			return true;
		else
			return super.equals(other);
	}

	public String toString(PrefixMapping pm, boolean quoting) {
		return "_:" + label.toString();
	}
}
