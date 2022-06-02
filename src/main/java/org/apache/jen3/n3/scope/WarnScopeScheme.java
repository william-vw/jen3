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
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.RDFObject;

public class WarnScopeScheme extends InfoScopeScheme {

	public WarnScopeScheme(N3Model model) {
		super(model);
	}

	@Override
	protected <X extends RDFObject> X iriPriorVar(Node iri, RDFObject var, Class<X> interf) {
		logger.warn(iriPriorVarMsg(iri, var));

		// option here to overwrite prior entry or don't cache this new IRI
		return model.createCached(iri, interf);
	}

	@Override
	protected <X extends RDFObject> X varPriorIri(Node var, RDFObject iri, Class<X> interf) {
		logger.warn(varPriorIriMsg(var, iri));

		// option here to overwrite prior entry or don't cache this new var
		return model.createCached(var, interf);
	}

	@Override
	protected <X extends RDFObject> X varPriorVar(Node newVarNode, RDFObject priorVarObj, Class<X> interf) {
		String msg = varPriorVarMsg(newVarNode, priorVarObj);
		if (msg != null)
			logger.warn(msg);

		// option here to overwrite prior entry or don't cache this new var
		return model.createCached((Node_QuantifiedVariable) newVarNode, interf);
	}
}