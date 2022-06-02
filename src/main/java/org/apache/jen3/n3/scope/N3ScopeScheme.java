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

package org.apache.jen3.n3.scope;

import org.apache.jen3.graph.Node;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class N3ScopeScheme {

	protected static Logger logger = LoggerFactory.getLogger(N3ScopeScheme.class);

	public enum N3ScopeTypes {
		WARN, ERROR,NONE;
	}

	public static N3ScopeScheme create(N3ScopeTypes type, N3Model model) {
		switch (type) {
		case WARN:
			return new WarnScopeScheme(model);

		case ERROR:
			return new ErrorScopeScheme(model);
			
		case NONE:
			return null;
		}

		return null;
	}

	protected N3Model model;

	public N3ScopeScheme(N3Model model) {
		this.model = model;
	}

	public <X extends Resource> X asResource(Node n, Class<X> interf) {
		// is it cached anywhere (i.e., possibly in outer models)?
		// (asNeedle; only interested in matching name here)
		RDFObject o = model.getCachedNested(n.asNeedle());

		// if not, let others take care of it
		if (o == null)
			return null;

		switch (n.getType()) {
		case URI:
			switch (o.getNodeType()) {

			case QUANT_VAR:
			case QUICK_VAR:
				return iriPriorVar(n, o, interf);

			default:
				break;
			}

			break;

		case QUANT_VAR:
		case QUICK_VAR:

			switch (o.getNodeType()) {
			case URI:
				return varPriorIri(n, o, interf);

			case QUANT_VAR:
			case QUICK_VAR:
				return varPriorVar(n, o, interf);

			default:
				break;
			}

			break;

		default:
			break;
		}

		return null;
	}

	protected abstract <X extends RDFObject> X iriPriorVar(Node iri, RDFObject var, Class<X> interf);

	protected abstract <X extends RDFObject> X varPriorIri(Node var, RDFObject iri, Class<X> interf);

	protected abstract <X extends RDFObject> X varPriorVar(Node newVar, RDFObject priorVar, Class<X> interf);
}
