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

package org.apache.jen3.reasoner.rulesys.impl;

import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.BindingEnvironment;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.reasoner.rulesys.Rule;

/**
 * Implementation of the binding environment interface for use in LP backward
 * rules.
 */
public class LPBindingEnvironment implements BindingEnvironment {

	/** The interpreter which holds the context for this environment */
	protected LPInterpreter interpreter;

	/**
	 * Constructor.
	 */
	public LPBindingEnvironment(LPInterpreter interpeter) {
		this.interpreter = interpeter;
	}

	/**
	 * Return the most ground version of the node. If the node is not a variable
	 * just return it, if it is a variable bound in this environment return the
	 * binding, if it is an unbound variable return the variable.
	 */
	@Override
	public Node ground(Node node) {
		return LPInterpreter.deref(node);
	}
	
	@Override
	public Node groundShallow(Node node) {
		return ground(node);
	}

	@Override
	public boolean isBound(Node node) {
		return ground(node) != node;
	}

	/**
	 * Bind a variable in the current environment to the given value. Checks that
	 * the new binding is compatible with any current binding.
	 * 
	 * @param var   a Node_RuleVariable defining the variable to bind
	 * @param value the value to bind
	 * @return false if the binding fails
	 */
	@Override
	public boolean bind(Node var, Node value) {
		Node dvar = var;
		if (dvar instanceof Node_RuleVariable)
			dvar = ((Node_RuleVariable) dvar).deref();
		if (dvar instanceof Node_RuleVariable) {
			interpreter.bind(dvar, value);
			return true;
		} else {
			return var.sameValueAs(value);
		}

	}

	/**
	 * Instantiate a triple pattern against the current environment. This version
	 * handles unbound variables by turning them into bNodes.
	 * 
	 * @param pattern the triple pattern to match
	 * @return a new, instantiated triple
	 */
	@Override
	public Triple instantiateOutput(TriplePattern pattern, Rule rule) {
		Node s = ground(pattern.getSubject());
		if (s.isVariable())
			s = NodeFactory.createBlankNode();
		Node p = ground(pattern.getPredicate());
		if (p.isVariable())
			p = NodeFactory.createBlankNode();
		Node o = ground(pattern.getObject());
		if (o.isVariable())
			o = NodeFactory.createBlankNode();
		return new Triple(s, p, o);
	}

	@Override
	public List<Triple> instantiateOutput(Rule rule) {
		throw new UnsupportedOperationException();
	}
}
