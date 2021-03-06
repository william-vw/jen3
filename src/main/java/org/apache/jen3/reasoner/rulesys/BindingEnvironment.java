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

package org.apache.jen3.reasoner.rulesys;

import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.TriplePattern;

/**
 * Interface through which the current bound values of variables can be found.
 * Many of the details vary between the forward and backward chaining system -
 * this interface is the minimal one needed by most builtins the specific
 * implementations offer richer functionality.
 * 
 */
public interface BindingEnvironment {

	/**
	 * Return the most ground version of the node. If the node is not a variable
	 * just return it, if it is a variable bound in this environment return the
	 * binding, if it is an unbound variable return the variable.
	 */
	public Node ground(Node node);

	/**
	 * Return the most ground version of the node, but make no effort to do this
	 * recursively for formulas or collections.
	 */
	public Node groundShallow(Node node);

	/**
	 * Returns whether this is a variable and it is bound in this environment.
	 * 
	 * @param node
	 * @return
	 */

	public boolean isBound(Node node);

	/**
	 * Bind a variable in the current environment to the given value. Checks that
	 * the new binding is compatible with any current binding.
	 * 
	 * @param var   a Node_RuleVariable defining the variable to bind
	 * @param value the value to bind
	 * @return false if the binding fails
	 */
	public boolean bind(Node var, Node value);

	/**
	 * Instantiate a triple pattern against the current environment. This version
	 * handles unbound variables by turning them into bNodes.
	 * 
	 * @param pattern the triple pattern to match
	 * @param rule    the associated rule object (needed for N3 rules)
	 * @return a new, instantiated triple
	 */
	public Triple instantiateOutput(TriplePattern pattern, Rule rule);

	/**
	 * Instantiate entire rule output (i.e., head) against the current environment
	 * in one go. This method is needed for N3 rules -
	 * see @{BindingStack.InstantiateTriplePatternVisitor}.
	 * 
	 * @param rule rule with head to instantiate
	 * @return a new, instantiated triple
	 */
	public List<Triple> instantiateOutput(Rule rule);
}
