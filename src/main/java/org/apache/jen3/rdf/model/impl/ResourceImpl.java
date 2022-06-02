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

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.enhanced.EnhGraph;
import org.apache.jen3.enhanced.EnhNode;
import org.apache.jen3.enhanced.Implementation;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.rdf.model.AnonId;
import org.apache.jen3.rdf.model.CitedFormula;
import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.HasNoModelException;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.LiteralRequiredException;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.NodeTypeRequiredException;
import org.apache.jen3.rdf.model.QuantifiedVariable;
import org.apache.jen3.rdf.model.Quantifier;
import org.apache.jen3.rdf.model.QuickVariable;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.RDFVisitor;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceRequiredException;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.shared.JenaException;
import org.apache.jen3.shared.NotFoundException;
import org.apache.jen3.vocabulary.RDF;

/** An implementation of Resource.
 */

public class ResourceImpl extends EnhNode implements Resource, Comparable<Resource> {
    
    final static public Implementation factory = new Implementation() {
        @Override
        public boolean canWrap( Node n, EnhGraph eg )
            { return !n.isLiteral(); }
        @Override
        public EnhNode wrap(Node n,EnhGraph eg) {
            if (n.isLiteral()) throw new ResourceRequiredException( n );
            return new ResourceImpl(n,eg);
        }
    };
    final static public Implementation rdfNodeFactory = new Implementation() {
        @Override
        public boolean canWrap( Node n, EnhGraph eg )
            { return true; }
        @Override
        public EnhNode wrap(Node n,EnhGraph eg) {
		if ( n.isURI() || n.isBlank() )
		  return new ResourceImpl(n,eg);
		if ( n.isLiteral() )
		  return new LiteralImpl(n,eg);
		return null;
	}
};

    /**
        the master constructor: make a new Resource in the given model,
        rooted in the given node.
    
        NOT FOR PUBLIC USE - used in ModelCom [and ContainerImpl]
    */
     public ResourceImpl( Node n, ModelCom m ) {
        super( n, m );
    }

    /** Creates new ResourceImpl */

    public ResourceImpl() {
        this( (ModelCom) null );
    }

    public ResourceImpl( ModelCom m ) {
        this( fresh( null ), m );
    }

     
    public ResourceImpl( Node n, EnhGraph m ) {
        super( n, m );
    }

    public ResourceImpl( String uri ) {
        super( fresh( uri ), null );
    }

    public ResourceImpl(String nameSpace, String localName) {
        super( NodeFactory.createURI( nameSpace + localName ), null );
    }

    public ResourceImpl(AnonId id) {
        this( id, null );
    }

    public ResourceImpl(AnonId id, ModelCom m) {
        this( NodeFactory.createBlankNode( id.getLabelString() ), m );
    }

    public ResourceImpl(String uri, ModelCom m) {
        this( fresh( uri ), m );
    }
    
    public ResourceImpl( Resource r, ModelCom m ) {
        this( r.asNode(), m );
    }
    
    public ResourceImpl(String nameSpace, String localName, ModelCom m) {
        this( NodeFactory.createURI( nameSpace + localName ), m );
    }
    
    @Override
    public boolean doMatchAbsolute() {
    	return node.doMatchAbsolute();
    }

    @Override
	public void setMatchAbsolute( boolean matchAbsolute ) {
    	node.setMatchAbsolute(matchAbsolute);
    }

	@Override
    public Object visitWith( RDFVisitor rv )
        { return isAnon() ? rv.visitBlank( this, getId() ) : rv.visitURI( this, getURI() ); }
        
    @Override
    public Resource asResource()
        { return this; }
    
    @Override
    public Literal asLiteral()
        { throw new LiteralRequiredException( asNode() ); }

    @Override
	public CitedFormula asCitedFormula() 
		{ throw new NodeTypeRequiredException(this, "Formula"); }

    @Override
	public QuantifiedVariable asQuantifiedVariable() 
		{ throw new NodeTypeRequiredException(this, "QuantifiedVariable"); }

    @Override
	public QuickVariable asQuickVariable() 
		{ throw new NodeTypeRequiredException(this, "QuickVariable"); }
    
    @Override
	public Collection asCollection()
		{ throw new NodeTypeRequiredException(this, "Collection"); }
    
    @Override
    public Quantifier asQuantifier()
        { throw new NodeTypeRequiredException( asNode(), "Quantifier" ); }
    
    @Override
    public Resource inModel( Model m )
        { 
        return 
            getModel() == m ? this 
            : isAnon() ? m.createResource( getId() ) 
            : asNode().isConcrete() == false ? (Resource) m.getRDFObject( asNode() )
            : m.createResource( getURI() ); 
        }
    
    private static Node fresh( String uri )
        { return uri == null ? NodeFactory.createBlankNode() : NodeFactory.createURI( uri ); }

    @Override
    public AnonId getId() 
        { return new AnonId(asNode().getBlankNodeId()); }

    @Override
    public String  getURI() {
        return isAnon() ? null : node.getURI();
    }

    @Override
    public String getNameSpace() {
        return isAnon() ? null : node.getNameSpace();
    }
    
	@Override
    public String getLocalName() {
        return isAnon() ? null : node.getLocalName(); 
    }

    @Override
    public boolean hasURI( String uri ) 
        { return node.hasURI( uri ); }
    
    @Override
    public String toString() 
        { return asNode().toString(); }
    
    @Override
    public int compareTo(Resource r) {
    	return toString().compareTo(r.toString());
    }
    
	protected ModelCom mustHaveModel()
		{
        ModelCom model = getModelCom();
		if (model == null) throw new HasNoModelException( this );
		return model;
		}
		    
    @Override
    public Statement getRequiredProperty(Resource p) 
    	{ return mustHaveModel().getRequiredProperty( this, p ); }

    @Override
    public Statement getRequiredProperty( final Resource p, final String lang )
        {
        final StmtIterator it = listProperties(p, lang) ; 
        if (!it.hasNext()) throw new NotFoundException( p.toString() );
        return it.next();
        }

    @Override
    public Statement getProperty( Resource p )
        { return mustHaveModel().getProperty( this, p ); }

    @Override
    public Statement getProperty( final Resource p, final String lang )
        {
        final StmtIterator it = listProperties(p, lang) ; 
        return it.hasNext() ? it.next() : null;
        }

    @Override
    public StmtIterator listProperties(Resource p) 
		{ 
		setMatchAbsolute(true);
		StmtIterator it = mustHaveModel().listStatements( this, p, (Resource) null ); 
		setMatchAbsolute(false);
		
		return it;
		}

    @Override
    public StmtIterator listProperties(Resource p, String lang) 
        { 
    	setMatchAbsolute(true);
		StmtIterator it = mustHaveModel().listStatements( this, p, null, lang ); 
		setMatchAbsolute(false);
		
		return it;
        }
    
    @Override
    public StmtIterator listProperties() 
    	{ 
    	setMatchAbsolute(true);
		StmtIterator it = mustHaveModel().listStatements( this, null, (Resource) null ); 
		setMatchAbsolute(false);
		
		return it;
    	}
    
    @Override
    public Resource addLiteral( Resource p, boolean o ) 
        {
        ModelCom m = mustHaveModel();
        m.add( this, p, m.createTypedLiteral( o ) );
        return this;
        }

    public Resource addProperty(Resource p, long o)  {
        mustHaveModel().addLiteral( this, p, o );
        return this;
    }
    
    @Override
    public Resource addLiteral( Resource p, long o ) 
        {
        Model m = mustHaveModel();
        m.add( this, p, m.createTypedLiteral( o ) );
        return this;
        }
    
    @Override
    public Resource addLiteral( Resource p, char o )  
        {
        ModelCom m = mustHaveModel();
        m.add( this, p, m.createTypedLiteral( o ) );
        return this;
        }

    public Resource addProperty(Resource p, float o) {
        mustHaveModel().addLiteral( this, p, o );
        return this;
    }

    public Resource addProperty(Resource p, double o) {
        mustHaveModel().addLiteral( this, p, o );
        return this;
    }
    
    @Override
    public Resource addLiteral( Resource p, double o ) 
        {
        Model m = mustHaveModel();
        m.add( this, p, m.createTypedLiteral( o ) );
        return this;
        }
    
    @Override
    public Resource addLiteral( Resource p, float o ) 
        {
        Model m = mustHaveModel();
        m.add( this, p, m.createTypedLiteral( o ) );
        return this;
        }

    @Override
    public Resource addProperty(Resource p, String o) {
        mustHaveModel().add( this, p, o );
        return this;
    }

    @Override
    public Resource addProperty(Resource p, String o, String l)
    {
        mustHaveModel().add( this, p, o, l );
        return this;
    }

    @Override
    public Resource addProperty(Resource p, String lexicalForm, RDFDatatype datatype)
    {
        mustHaveModel().add(this, p, lexicalForm, datatype) ;
        return this ;
    }

    @Override
    public Resource addLiteral( Resource p, Object o ) 
        {
        ModelCom m = mustHaveModel();
        m.add( this, p, m.createTypedLiteral( o ) );
        return this;
        }
    
    @Override
    public Resource addLiteral( Resource p, Literal o )
        {
        mustHaveModel().add( this, p, o );
        return this;
        }

    @Override
    public Resource addProperty( Resource p, Resource o ) 
        {
        mustHaveModel().add( this, p, o );
        return this;
        }

    @Override
    public boolean hasProperty(Resource p)  {
        return mustHaveModel().contains( this, p );
    }
    
    @Override
    public boolean hasLiteral( Resource p, boolean o )  
        {
        ModelCom m = mustHaveModel();
        return m.contains( this, p, m.createTypedLiteral( o ) );
        }
    
    @Override
    public boolean hasLiteral( Resource p, long o ) 
        {
        ModelCom m = mustHaveModel();
        return m.contains( this, p, m.createTypedLiteral( o ) );
        }
    
    @Override
    public boolean hasLiteral( Resource p, char o )  
        {
        ModelCom m = mustHaveModel();
        return m.contains( this, p, m.createTypedLiteral( o ) );
        }
    
    @Override
    public boolean hasLiteral( Resource p, double o ) 
        {
        ModelCom m = mustHaveModel();
        return m.contains( this, p, m.createTypedLiteral( o ) );
        }
    
    @Override
    public boolean hasLiteral( Resource p, float o ) 
        {
        ModelCom m = mustHaveModel();
        return m.contains( this, p, m.createTypedLiteral( o ) );
        }
    
    @Override
    public boolean hasProperty(Resource p, String o) {
        return mustHaveModel().contains( this, p, o );
    }

    @Override
    public boolean hasProperty(Resource p, String o, String l) {
        return mustHaveModel().contains( this, p, o, l );
    }

    @Override
    public boolean hasLiteral( Resource p, Object o ) 
        {
        ModelCom m = mustHaveModel();
        return m.contains( this, p, m.createTypedLiteral( o ) );
        }

    @Override
    public boolean hasProperty(Resource p, RDFObject o)  {
        return mustHaveModel().contains( this, p, o );
    }

    @Override
    public Resource removeProperties()  {
        removeAll(null);
        return this;
    }
    
    @Override
    public Resource removeAll( Resource p ) {
        mustHaveModel().removeAll( this, p, null );
        return this;
    }
    
    @Override
    public Resource begin()  {
        mustHaveModel().begin();
        return this;
    }

    @Override
    public Resource abort()  {
        mustHaveModel().abort();
        return this;
    }

    @Override
    public Resource commit()  {
        mustHaveModel().commit();
        return this;
    }

    @Override
    public Model getModel() {
        return (Model) getGraph();
    }
    
    protected ModelCom getModelCom()
        { return (ModelCom) getGraph(); }

    @Override
    public Resource getPropertyResourceValue(Resource p)
    {
        StmtIterator it = listProperties(p) ;
        try {
            while (it.hasNext())
            {
                RDFObject n = it.next().getObject() ;
                if (n.isResource()) return (Resource)n ;
            }
            return null ;
        } finally { it.close() ; }
    }
    
    protected int ordinal = -1;
    
    @Override
    public int getOrdinal()
        {
        if (ordinal < 0) 
            ordinal = computeOrdinal();
        return ordinal;
        }

    private int computeOrdinal()
        {
        String localName = getLocalName();
        if (getNameSpace().equals( RDF.getURI() ) && localName.matches( "_[0-9]+" )) 
            return parseInt( localName.substring( 1 ) );
        return 0;
        }

    private int parseInt( String digits )
        {
        try { return Integer.parseInt( digits );}
        catch (NumberFormatException e) { throw new JenaException( "checkOrdinal fails on " + digits, e ); }
        }
    
    @Override
	public boolean includesVars() {
		return false;
	}
}
