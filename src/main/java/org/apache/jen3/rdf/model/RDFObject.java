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

package org.apache.jen3.rdf.model;

import org.apache.jen3.graph.FrontsNode;
import org.apache.jen3.graph.NodeTypes;

/**
 * Interface covering RDF resources and literals. Allows probing whether a node
 * is a literal/[blank, URI]resource, moving nodes from model to model, and
 * viewing them as different Java types using the .as() polymorphism.
 */
public interface RDFObject extends FrontsNode {
	/**
	 * Answer a String representation of the node. The form of the string depends on
	 * the type of the node and is intended for human consumption, not machine
	 * analysis.
	 */
	@Override
	public String toString();

	/**
	 * @return type of node
	 */

	public NodeTypes getNodeType();

	/**
	 * Answer true iff this RDFNode is an anonymous resource. Useful for one-off
	 * tests: see also visitWith() for making literal/anon/URI choices.
	 */
	public boolean isAnon();

	/**
	 * Answer true iff this RDFNode is a literal resource. Useful for one-off tests:
	 * see also visitWith() for making literal/anon/URI choices.
	 */
	public boolean isLiteral();

	/**
	 * Answer true iff this RDFNode is an named resource. Useful for one-off tests:
	 * see also visitWith() for making literal/anon/URI choices.
	 */
	public boolean isURI();

	/**
	 * Answer true iff this RDFNode is a URI resource or an anonymous resource (ie
	 * is not a literal). Useful for one-off tests: see also visitWith() for making
	 * literal/anon/URI choices.
	 */
	public boolean isURIOrAnon();

	/**
	 * Answer true iff this RDFNode is a Resource.
	 * 
	 */
	public boolean isResource();

	/**
	 * Answer true iff this RDFNode is an N3 Formula resource.
	 * 
	 */
	public boolean isCitedFormula();

	/**
	 * Answer true iff this RDFNode is an N3 Quantified Variable resource.
	 * 
	 */
	public boolean isQuantifiedVariable();

	/**
	 * Answer true iff this RDFNode is an N3 Quick Variable resource.
	 * 
	 */
	public boolean isQuickVariable();

	/**
	 * Answer true iff this RDFNode is an N3 Collection resource.
	 * 
	 */
	public boolean isCollection();

	/**
	 * Answer true iff this RDFNode is an N3 quantifier.
	 */
	public boolean isQuantifier();

	/**
	 * RDFNodes can be converted to different implementation types. Convert this
	 * RDFNode to a type supporting the <code>view</code>interface. The resulting
	 * RDFNode should be an instance of <code>view</code> and should have any
	 * internal invariants as specified.
	 * <p>
	 * If the RDFNode has no Model attached, it can only be .as()ed to a type it
	 * (this particular RDFNode) already has.
	 * <p>
	 * If the RDFNode cannot be converted, an UnsupportedPolymorphism exception is
	 * thrown..
	 */
	public <T extends RDFObject> T as(Class<T> view);

	/**
	 * Answer true iff this RDFNode can be viewed as an instance of
	 * <code>view</code>: that is, if it has already been viewed in this way, or if
	 * it has an attached model in which it has properties that permit it to be
	 * viewed in this way. If <code>canAs</code> returns <code>true</code>,
	 * <code>as</code> on the same view should deliver an instance of that class.
	 */
	public <T extends RDFObject> boolean canAs(Class<T> view);

	/**
	 * Return the model associated with this resource. If the Resource was not
	 * created by a Model, the result may be null.
	 * 
	 * @return The model associated with this resource.
	 */
	public Model getModel();

	/**
	 * Answer a .equals() version of this node, except that it's in the model
	 * <code>m</code>.
	 * 
	 * @param m a model to move the node to
	 * @return this, if it's already in m (or no model), a copy in m otherwise
	 */
	public RDFObject inModel(Model m);

	/**
	 * Apply the appropriate method of the visitor to this node's content and return
	 * the result.
	 * 
	 * @param rv an RDFVisitor with a method for URI/blank/literal nodes
	 * @return the result returned by the selected method
	 */
	public Object visitWith(RDFVisitor rv);

	/**
	 * If this node is a Literal, answer that literal; otherwise throw an exception.
	 */
	public Literal asLiteral();

	/**
	 * If this node is a Resource, answer that resource; otherwise throw an
	 * exception.
	 */
	public Resource asResource();

	/**
	 * If this node is a formula, answer that formula; otherwise throw an exception.
	 */
	public CitedFormula asCitedFormula();

	/**
	 * If this node is a quantified variable, answer that var; otherwise throw an
	 * exception.
	 */
	public QuantifiedVariable asQuantifiedVariable();

	/**
	 * If this node is a quick-variable, answer that var; otherwise throw an
	 * exception.
	 */
	public QuickVariable asQuickVariable();

	/**
	 * If this node is a collection, answer that collection; otherwise throw an
	 * exception.
	 */
	public Collection asCollection();

	/**
	 * If this node is a quantifier, answer that quantifier; otherwise throw an
	 * exception.
	 */
	public Quantifier asQuantifier();
}
