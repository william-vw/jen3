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

package org.apache.jen3.rdf.model.test;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.test.NodeCreateUtils;
import org.apache.jen3.rdf.model.AnonId;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Property;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.impl.ModelCom;
import org.apache.jen3.shared.PropertyNotFoundException;

public abstract class AbstractTestModel extends ModelTestBase
    {
    public AbstractTestModel( String name )
        { super(name); }

    public abstract Model getModel();
    
    private Model model;
    
    @Override
    public void setUp()
        { model = getModel(); }
        
    @Override
    public void tearDown()
        { model.close(); } 
       
    public void testTransactions() {
        if ( model.supportsTransactions() )
            model.executeInTxn(() -> {}) ;
    }
        
    public void testCreateResourceFromNode()
        {
        RDFObject S = model.getRDFObject( NodeCreateUtils.create( "spoo:S" ) ); 
        assertInstanceOf( Resource.class, S );
        assertEquals( "spoo:S", ((Resource) S).getURI() );
        }
        
    public void testCreateLiteralFromNode()
        {
        RDFObject S = model.getRDFObject( NodeCreateUtils.create( "42" ) ); 
        assertInstanceOf( Literal.class, S );
        assertEquals( "42", ((Literal) S).getLexicalForm() );
        }    
            
   public void testCreateBlankFromNode()
        {
        RDFObject S = model.getRDFObject( NodeCreateUtils.create( "_Blank" ) ); 
        assertInstanceOf( Resource.class, S );
        assertEquals( new AnonId( "_Blank" ), ((Resource) S).getId() );
        }
        
    public void testIsEmpty()
        {
        Statement S1 = statement( model, "model rdf:type nonEmpty" );
        Statement S2 = statement( model, "pinky rdf:type Pig" );
        assertTrue( model.isEmpty() );
        model.add( S1 );
        assertFalse( model.isEmpty() );
        model.add( S2 );
        assertFalse( model.isEmpty() );
        model.remove( S1 );
        assertFalse( model.isEmpty() );
        model.remove( S2 );
        assertTrue( model.isEmpty() );
        }
        
    public void testContainsResource()
        {
        modelAdd( model, "x R y; _a P _b" );
        assertTrue( model.containsResource( resource( model, "x" ) ) );
        assertTrue( model.containsResource( resource( model, "R" ) ) );
        assertTrue( model.containsResource( resource( model, "y" ) ) );
        assertTrue( model.containsResource( resource( model, "_a" ) ) );
        assertTrue( model.containsResource( resource( model, "P" ) ) );
        assertTrue( model.containsResource( resource( model, "_b" ) ) );
        assertFalse( model.containsResource( resource( model, "i" ) ) );
        assertFalse( model.containsResource( resource( model, "_j" ) ) );
        }
        
    /**
        Test the new version of getProperty(), which delivers null for not-found
        properties.
    */
    public void testGetProperty()
        {
        modelAdd( model, "x P a; x P b; x R c" );
        Resource x = resource( model, "x" );
        assertEquals( resource( model, "c" ), x.getProperty( property( model, "R" ) ).getObject() );
        RDFObject ob = x.getProperty( property( model, "P" ) ).getObject();
        assertTrue( ob.equals( resource( model, "a" ) ) || ob.equals( resource( model, "b" ) ) );
        assertNull( x.getProperty( property( model, "noSuchPropertyHere" ) ) );
        }

    /**
     * Tests {@link Resource#getProperty(Property, String)} and
     * {@link Resource#getRequiredProperty(Property, String)}.
     */
    public void testGetPropertyWithLanguage()
        {
        model.createStatement(ModelTestBase.resource(model, "x"), ModelTestBase.property(model, "P"), "a", "pt");
        model.createStatement(ModelTestBase.resource(model, "x"), ModelTestBase.property(model, "P"), "b", "en");
        model.createStatement(ModelTestBase.resource(model, "x"), ModelTestBase.property(model, "P"), "c", "es");
        model.createStatement(ModelTestBase.resource(model, "x"), ModelTestBase.property(model, "R"), "d", "fr");
        model.createStatement(ModelTestBase.resource(model, "x"), ModelTestBase.property(model, "R"), "e", "de");

        {//Tests {@link Resource#getProperty(Property, String)}
        final Resource x = resource( model, "x" );
        assertEquals("a", x.getProperty( property( model, "P" ), "pt") );
        assertEquals("b", x.getProperty( property( model, "P" ), "en") );
        assertNull(x.getProperty( property( model, "P" ), "ja") );
        final Literal l = x.getProperty( property(model, "R") ).getLiteral();
        assertTrue( "d".equals( l.getString() ) || "e".equals( l.getString() ) );
        }

        {//Tests {@link Resource#getRequiredProperty(Property, String)}
        final Resource x = resource( model, "x" );
        assertEquals("a", x.getRequiredProperty( property( model, "P" ), "pt") );
        assertEquals("b", x.getRequiredProperty( property( model, "P" ), "en") );
        try {
            x.getRequiredProperty( property( model, "P" ), "ja");
            fail("Must thrown PropertyNotFoundException.");
        } catch (PropertyNotFoundException e) {}
        final Literal l = x.getRequiredProperty( property(model, "R") ).getLiteral();
        assertTrue( "d".equals( l.getString() ) || "e".equals( l.getString() ) );
        assertTrue("de".equals( l.getLanguage() ) || "fr".equals( l.getLanguage() ) );
        }
        }

    public void testToStatement()
        {
        Triple t = triple( "a P b" );
        Statement s = model.asStatement( t );
        assertEquals( node( "a" ), s.getSubject().asNode() );
        assertEquals( node( "P" ), s.getPredicateProperty().asNode() );
        assertEquals( node( "b" ), s.getObject().asNode() );
        }
    
    public void testAsRDF()
        {
        testPresentAsRDFNode( node( "a" ), Resource.class );
        testPresentAsRDFNode( node( "17" ), Literal.class );
        testPresentAsRDFNode( node( "_b" ), Resource.class );
        }

    private void testPresentAsRDFNode( Node n, Class<? extends RDFObject> nodeClass )
        {
        RDFObject r = model.asRDFObject( n );
        assertSame( n, r.asNode() );
        assertInstanceOf( nodeClass, r );
        }
        
    public void testURINodeAsResource()
        {
        Node n = node( "a" );
        Resource r = model.wrapAsResource( n );
        assertSame( n, r.asNode() );
        }
        
    public void testLiteralNodeAsResourceFails()
        {
        try 
            {
            model.wrapAsResource( node( "17" ) );
            fail( "should fail to convert literal to Resource" );
            }
        catch (UnsupportedOperationException e)
            { pass(); }
        }
    
    public void testRemoveAll()
        {
        testRemoveAll( "" );
        testRemoveAll( "a RR b" );
        testRemoveAll( "x P y; a Q b; c R 17; _d S 'e'" );
        testRemoveAll( "subject Predicate 'object'; http://nowhere/x scheme:cunning not:plan" );
        }
    
    protected void testRemoveAll( String statements )
        {
        modelAdd( model, statements );
        assertSame( model, model.removeAll() );
        assertEquals( "model should have size 0 following removeAll(): ", 0, model.size() );
        }
    
    /**
 		Test cases for RemoveSPO(); each entry is a triple (add, remove, result).
	 	<ul>
	 	<li>add - the triples to add to the graph to start with
	 	<li>remove - the pattern to use in the removal
	 	<li>result - the triples that should remain in the graph
	 	</ul>
	*/
	protected String[][] cases =
		{
	            { "x R y", "x R y", "" },
	            { "x R y; a P b", "x R y", "a P b" },
	            { "x R y; a P b", "?? R y", "a P b" },
	            { "x R y; a P b", "x R ??", "a P b" },
	            { "x R y; a P b", "x ?? y", "a P b" },      
	            { "x R y; a P b", "?? ?? ??", "" },       
	            { "x R y; a P b; c P d", "?? P ??", "x R y" },       
	            { "x R y; a P b; x S y", "x ?? ??", "a P b" },                 
		};
    
    /**
 	Test that remove(s, p, o) works, in the presence of inferencing graphs that
 	mean emptyness isn't available. This is why we go round the houses and
 	test that expected ~= initialContent + addedStuff - removed - initialContent.
 	*/
	public void testRemoveSPO()
	    {
	    ModelCom mc = (ModelCom) ModelFactory.createDefaultModel();
            for ( String[] aCase : cases )
            {
                for ( int j = 0; j < 3; j += 1 )
                {
                    Model content = getModel();
                    Model baseContent = copy( content );
                    modelAdd( content, aCase[0] );
                    Triple remove = triple( aCase[1] );
                    Node s = remove.getSubject(), p = remove.getPredicate(), o = remove.getObject();
                    Resource S = (Resource) ( s.equals( Node.ANY ) ? null : mc.getRDFObject( s ) );
                    Property P = ( ( p.equals( Node.ANY ) ? null : mc.getRDFObject( p ).as( Property.class ) ) );
                    Resource O = o.equals( Node.ANY ) ? null : mc.getRDFObject( o ).asResource();
                    Model expected = modelWithStatements( aCase[2] );
                    content.removeAll( S, P, O );
                    Model finalContent = copy( content ).remove( baseContent );
                    assertIsoModels( aCase[1], expected, finalContent );
                }
            }
	    }
	
    public void testIsClosedDelegatedToGraph()
        {
        Model m = getModel();
        assertFalse( m.isClosed() );
        m.close();
        assertTrue( m.isClosed() );
        }
    
	protected Model copy( Model m )
	    {
	    return ModelFactory.createDefaultModel().add( m );
	    }
    }
