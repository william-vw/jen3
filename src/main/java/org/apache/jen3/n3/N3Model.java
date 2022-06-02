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

package org.apache.jen3.n3;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.graph.n3.scope.ScopedObject;
import org.apache.jen3.n3.impl.N3ModelImpl.N3EventListener;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.InfModel;
import org.apache.jen3.rdf.model.QuickVariable;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.Resource;

/**
 * 
 * An enhanced view of a Jena model that contains N3 data.
 * 
 * @author wvw
 *
 */

public interface N3Model extends InfModel, ScopedObject {

	/**
	 * 
	 * @return the spec passed when creating this model
	 */

	public N3ModelSpec getSpec();

	/**
	 * Set the scope for this N3 model. Per default, the new scope will be
	 * propagated to the model's elements.
	 * 
	 * @param scope
	 */

	public void setScope(Scope scope);

	public Scope getScope();

	RDFObject getCachedNested(Node n);

	public void prepared();

	/**
	 * Set this model as immutable. Used by {@link CitedFormula} for ensuring its
	 * own immutability.
	 * 
	 */
	public void setImmutable();

	/**
	 * @return whether this model is immutable
	 */

	public boolean isImmutable();

	public N3Model copy();

	public void setListener(N3EventListener listener);
	
	public <X extends RDFObject> X create(Node n, Class<X> interf);

	public RDFObject getCached(Node n);

	public <X extends RDFObject> X createCached(Node n, Class<X> interf);

	/**
	 * Answer an empty N3 formula.
	 * 
	 * @return
	 */
	public CitedFormula createCitedFormula();

	public CitedFormula createCitedFormula(N3Model model);

	public CitedFormula createCitedFormula(N3ModelSpec spec);

	/**
	 * Answer a new N3 quick-variable with the given name. Will always be scoped on
	 * outer scope.
	 * 
	 * @param name
	 * @return
	 */

	public QuickVariable createQuickVariable(String name);

	/**
	 * Answer an empty N3 collection.
	 * 
	 * @return
	 */
	public Collection createCollection();

	/**
	 * Answer a new N3 collection with the given Node elements - these will be
	 * wrapped by the collection.
	 * 
	 * @return
	 */
	public Collection createCollection(List<Resource> elements);

	/**
	 * Answer a new N3 collection with the given Node elements - these will be
	 * wrapped by the collection.
	 * 
	 * @return
	 */
	public Collection createCollection(Resource... elements);

	/**
	 * In case the model contains any log:outputString statements, this method
	 * concatenates them based on the ordering of their subject, and returns the
	 * concatenated string.
	 * 
	 * See https://www.w3.org/2000/10/swap/doc/CwmBuiltins
	 * 
	 * 
	 * @return concatenated string based on log:outputString statements
	 */
	public String outputString();

	/**
	 * Print the result of {@link #outputString()} to the given outputstream.
	 * 
	 */
	public void outputString(OutputStream out) throws IOException;
}
