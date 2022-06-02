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

package org.apache.jen3.n3.io;

import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.Quantifier;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.shared.JenaException;

/**
 * N3 writer supporting the full N3 syntax (incl. formulas, collections,
 * quick-vars, quantifiers, ..)
 */

public class N3JenaWriterFull extends N3JenaWriterCommon {

	public N3JenaWriterFull(boolean usePrefixes) {
		this();
		
		this.usePrefixes = usePrefixes;
	}
	
	public N3JenaWriterFull() {
		this.alwaysAllocateBNodeLabel = true;
	}

	public N3JenaWriterFull(BNodeWriter writer) {
		super(writer);

		alwaysAllocateBNodeLabel = true;
	}

	@Override
	protected void writeModel(Model model, boolean nested) throws IOException {
		if (!(model instanceof N3Model))
			throw new JenaException("Require an N3 model to write N3 data. "
					+ "Please use ModelFactory.createN3Model to create an N3 model.");

		// in case quantifiers have formula-wide scope
		// (unclear at this point)
		printQuantifiers((N3Model) model, "");

		if (nested)
			writePerStatement(model);
		else
			writePerSubject(model);
	}

	// this is important for isomorphism
	// cited formulas are identical strictly based on their contents
	// i.e., ordering matters, printing per subject upsets the ordering ..
	
	protected void writePerStatement(Model model) throws IOException {
		StmtIterator stmtIt = model.listStatements();

		while (stmtIt.hasNext()) {
			Statement stmt = stmtIt.next();

			out.incIndent(indentProperty);

			writeStatement(stmt);

			out.decIndent(indentProperty);
			out.println(" .");
		}
	}

	protected void writeStatement(Statement stmt) {
		writeSubject(stmt.getSubject());

		writePredicateObject(stmt.getPredicate(), stmt.getObject());
	}

	@Override
	public String formatNode(RDFObject node) {
		return formatResource(node.asResource());
	}

	private void printQuantifiers(N3Model model, String indent) {
		Scope scope = model.getScope();

		String qstr = scope.getChildren().stream()
				.filter(s -> s.getLvl() == scope.getLvl() && s.getScoped() instanceof Quantifier)
				.map(s -> (Quantifier) s.getScoped()).distinct().map(q -> formatQuantifier(q)).filter(s -> s != null)
				.collect(Collectors.joining("\n" + indent));

		if (!qstr.isEmpty())
			out.println(indent + qstr);
	}
	
	@Override
	protected N3JenaWriterCommon copy() {
		N3JenaWriterCommon ret = new N3JenaWriterFull(bnodeWriter);
		ret.alwaysAllocateBNodeLabel = this.alwaysAllocateBNodeLabel;
		ret.usePrefixes = this.usePrefixes;
		
		return ret;
	}
}