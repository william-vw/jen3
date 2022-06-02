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
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.NodeTypeRequiredException;
import org.apache.jen3.rdf.model.QuickVariable;

public class QuickVariableImpl extends QuantifiedVariableImpl implements QuickVariable {

	final static public Implementation factory = new Implementation() {
		@Override
		public boolean canWrap(Node n, EnhGraph eg) {
			return n.isQuickVariable();
		}

		@Override
		public EnhNode wrap(Node n, EnhGraph eg) {
			// rule-var: will happen when a rule has been inferred
			// (n3 rule parser will convert quick-vars to rule-vars)

			if (n.isQuantifiedVariable())
				return new QuickVariableImpl(n, eg);

			else if (n.isRuleVariable()) {
				N3Model m = (N3Model) eg;
				
				return (QuickVariableImpl) m.createQuickVariable(((Node_Variable) n).getName());

			} else
				throw new NodeTypeRequiredException(n, "QuickVariable | RuleVariable");
		}
	};

	public QuickVariableImpl() {
		this((ModelCom) null);
	}

	public QuickVariableImpl(ModelCom model) {
		super(model);
	}

	public QuickVariableImpl(Node n, ModelCom m) {
		super(n, m);
	}

	public QuickVariableImpl(Node n, EnhGraph g) {
		super(n, g);
	}

	@Override
	public QuickVariable asQuickVariable() {
		return this;
	}
}
