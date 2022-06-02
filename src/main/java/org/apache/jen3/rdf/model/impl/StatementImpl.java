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
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.rdf.model.Alt;
import org.apache.jen3.rdf.model.Bag;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.LiteralRequiredException;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Property;
import org.apache.jen3.rdf.model.RDFList;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.RSIterator;
import org.apache.jen3.rdf.model.ReifiedStatement;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceF;
import org.apache.jen3.rdf.model.Seq;
import org.apache.jen3.rdf.model.Statement;

/**
 * An implementation of Statement.
 */
public class StatementImpl extends StatementBase implements Statement {

	protected Resource subject;
	protected Resource predicate;
	protected Resource object;

	/** Creates new StatementImpl */
	public StatementImpl(RDFObject subject, RDFObject predicate, RDFObject object, ModelCom model) {
		super(model);
		this.subject = subject.inModel(model).asResource();
		this.predicate = predicate.inModel(model).asResource();
		this.object = object.inModel(model).asResource();
	}

	// TODO fix this hack
	protected static ModelCom empty = (ModelCom) ModelFactory.createDefaultModel();

	public StatementImpl(RDFObject subject, RDFObject predicate, RDFObject object) {
		super(empty);
		this.subject = subject.inModel(model).asResource();
		this.predicate = predicate.inModel(model).asResource();
		this.object = object.inModel(model).asResource();
	}

	/**
	 * create a Statement from the triple _t_ in the enhanced graph _eg_. The
	 * Statement has subject, predicate, and object corresponding to those of _t_.
	 */
	public static Statement toStatement(Triple t, ModelCom eg) {
		RDFObject s = eg.asResource(t.getSubject());
		RDFObject p = eg.asResource(t.getPredicate());
		RDFObject o = eg.asResource(t.getObject());
		return new StatementImpl(s, p, o, eg);
	}

	@Override
	public Resource get(byte position) {
		switch (position) {

		case S:
			return subject;
		case P:
			return predicate;
		case O:
			return object;
		}

		return null;
	}

	@Override
	public Resource getSubject() {
		return subject;
	}

	@Override
	public Resource getPredicate() {
		return predicate;
	}

	@Override
	public Property getPredicateProperty() {
		return mustBeProperty(predicate);
	}

	@Override
	public Resource getObject() {
		return object;
	}

	@Override
	public Statement getStatementProperty(Resource p) {
		return asResource().getRequiredProperty(p);
	}

	@Override
	public Resource getResource() {
		return mustBeResource(object);
	}

	@Override
	@Deprecated
	public Resource getResource(ResourceF f) {
		return f.createResource(getResource());
	}

	@Override
	public Statement getProperty(Resource p) {
		return getResource().getRequiredProperty(p);
	}

	/**
	 * get the object field of this statement, insisting that it be a Literal. If it
	 * isn't, throw LiteralRequiredException.
	 */
	@Override
	public Literal getLiteral() {
		if (object instanceof Literal) {
			return (Literal) object;
		} else {
			throw new LiteralRequiredException(object);
		}
	}

	@Override
	public Bag getBag() {
		return object.as(Bag.class);
	}

	@Override
	public Alt getAlt() {
		return object.as(Alt.class);
	}

	@Override
	public Seq getSeq() {
		return object.as(Seq.class);
	}

	@Override
	public RDFList getList() {
		return object.as(RDFList.class);
	}

	/** it turns out to be handy to return the new StatementImpl as the result */
	@Override
	protected StatementImpl replace(RDFObject n) {
		StatementImpl s = new StatementImpl(subject, predicate, n, model);
		model.remove(this).add(s);
		return s;
	}

	/**
	 * .equals() defers to .sameAs so we only get the complexity of one cast.
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof Statement && sameAs((Statement) o);
	}

	/**
	 * sameAs - is this statement equal to the statement o? We can't assume o is a
	 * StatementImpl
	 */
	private final boolean sameAs(Statement o) {
		return subject.equals(o.getSubject()) && predicate.equals(o.getPredicate()) && object.equals(o.getObject());
	}

	@Override
	public int hashCode() {
		return asTriple().hashCode();
	}

	public Resource asResource() {
		return model.getAnyReifiedStatement(this);
	}

	@Override
	public Statement remove() {
		model.remove(this);
		return this;
	}

	@Override
	public void removeReification() {
		model.removeAllReifications(this);
	}

	@Override
	public Triple asTriple() {
		return Triple.create(subject.asNode(), predicate.asNode(), object.asNode());
	}

	/**
	 * returns an array of triples corresponding to the array of statements; ie the
	 * i'th element of the result is the i'th element of the input as a triple.
	 * 
	 * @param statements the array of statements to convert
	 * @return the corresponding array of triples
	 */
	public static Triple[] asTriples(Statement[] statements) {
		Triple[] triples = new Triple[statements.length];
		for (int i = 0; i < statements.length; i += 1)
			triples[i] = statements[i].asTriple();
		return triples;
	}

	@Override
	public boolean isReified() {
		return model.isReified(this);
	}

	/**
	 * create a ReifiedStatement corresponding to this Statement
	 */
	@Override
	public ReifiedStatement createReifiedStatement() {
		return ReifiedStatementImpl.create(this);
	}

	/**
	 * create a ReifiedStatement corresponding to this Statement and with the given
	 * _uri_.
	 */
	@Override
	public ReifiedStatement createReifiedStatement(String uri) {
		return ReifiedStatementImpl.create((ModelCom) this.getModel(), uri, this);
	}

	@Override
	public RSIterator listReifiedStatements() {
		return model.listReifiedStatements(this);
	}

	/**
	 * create an RDF node which might be a literal, or not.
	 */
	public static RDFObject createObject(Node n, EnhGraph g) {
		return n.isLiteral() ? (RDFObject) new LiteralImpl(n, g) : new ResourceImpl(n, g);
	}
}
