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

package org.apache.jen3.rdfxml.xmloutput;

import java.io.*;

import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.rdf.model.test.ModelTestBase;
import org.apache.jen3.shared.CannotEncodeCharacterException;
import org.apache.jen3.vocabulary.RDF;

/**
     Tests to ensure that certain literals are either encoded properly or reported
     as exceptions.
*/
public class TestLiteralEncoding extends ModelTestBase
    {
    public TestLiteralEncoding( String name )
        { super( name ); }
    
    public void testX()
        {
        assertEquals( "", TermUtil.substituteEntitiesInElementContent( "" ) );
        assertEquals( "abc", TermUtil.substituteEntitiesInElementContent( "abc" ) );
        assertEquals( "a&lt;b", TermUtil.substituteEntitiesInElementContent( "a<b" ) );
        assertEquals( "a&gt;b", TermUtil.substituteEntitiesInElementContent( "a>b" ) );
        assertEquals( "a&amp;b", TermUtil.substituteEntitiesInElementContent( "a&b" ) );
        assertEquals( "a;b", TermUtil.substituteEntitiesInElementContent( "a;b" ) );
        assertEquals( "a b", TermUtil.substituteEntitiesInElementContent( "a b" ) );
        assertEquals( "a\nb", TermUtil.substituteEntitiesInElementContent( "a\nb" ) );
        assertEquals( "a'b", TermUtil.substituteEntitiesInElementContent( "a'b" ) );
    //
        assertEquals( "a&lt;b&lt;c", TermUtil.substituteEntitiesInElementContent( "a<b<c" ) );
        assertEquals( "a&lt;b&gt;c", TermUtil.substituteEntitiesInElementContent( "a<b>c" ) );
        assertEquals( "a&lt;b&amp;c", TermUtil.substituteEntitiesInElementContent( "a<b&c" ) );
        assertEquals( "a&amp;b&amp;c", TermUtil.substituteEntitiesInElementContent( "a&b&c" ) );
        assertEquals( "a&amp;b&gt;c", TermUtil.substituteEntitiesInElementContent( "a&b>c" ) );
        assertEquals( "a&amp;b&lt;c", TermUtil.substituteEntitiesInElementContent( "a&b<c" ) );
    //
        // Encoding in content output : protect CR but raw NL is fine. 
        assertEquals( "&#xD;", TermUtil.substituteEntitiesInElementContent( "\r" ) );
        assertEquals( "\n", TermUtil.substituteEntitiesInElementContent( "\n" ) );
    //
        assertEquals( "", TermUtil.substituteStandardEntities( "" ) );
        assertEquals( "&lt;", TermUtil.substituteStandardEntities( "<" ) );
        assertEquals( "&gt;", TermUtil.substituteStandardEntities( ">" ) );
        assertEquals( "&amp;", TermUtil.substituteStandardEntities( "&" ) );
        assertEquals( "&apos;", TermUtil.substituteStandardEntities( "\'" ) );
        assertEquals( "&quot;", TermUtil.substituteStandardEntities( "\"" ) );
        assertEquals( "&#xA;", TermUtil.substituteStandardEntities( "\n" ) );
        assertEquals( "&#xD;", TermUtil.substituteStandardEntities( "\r" ) );
        assertEquals( "&#9;", TermUtil.substituteStandardEntities( "\t" ) );
    //
        assertEquals( "a&lt;b&amp;c&gt;d", TermUtil.substituteStandardEntities( "a<b&c>d" ) );
        }
    
    public void testLexicalEncodingException(String lang)
        {
        for (char ch = 0; ch < 32; ch += 1) 
            if (ch != '\n' && ch != '\t' && ch != '\r')
                testThrowsBadCharacterException( ch, lang );
        testThrowsBadCharacterException( (char)0xFFFF, lang );
        testThrowsBadCharacterException( (char)0xFFFE, lang );
        
        }
    
    
    public void testBasicLexicalEncodingException()
    {
    	testLexicalEncodingException("RDF/XML");
    }
    
    // TODO: add test for bad char in property attribute.
    public void testPrettyLexicalEncodingException()
    {
    	testLexicalEncodingException("RDF/XML-ABBREV");
    }
    private void testThrowsBadCharacterException( char badChar, String lang )
        {
        String badString = "" + badChar;

        Model m = ModelFactory.createDefaultModel();
        m.createResource().addProperty(RDF.value, badString);
        Writer w = new Writer(){
			@Override
            public void close() throws IOException {}
			@Override
            public void flush() throws IOException {}
			@Override
            public void write(char[] arg0, int arg1, int arg2) throws IOException {}
        };
        try 
            { 
        	m.write(w,lang);
//            Util.substituteEntitiesInElementContent( badString ); 
            fail( "should trap bad character: (char)" + (int) badChar ); 
            }
        catch (CannotEncodeCharacterException e) 
            { 
            assertEquals( badChar, e.getBadChar() ); 
            assertEquals( "XML", e.getEncodingContext() );
            }
        }
    
    public void testNoApparentCData()
        {
        Model m = modelWithStatements( "a R ']]>'" );
        StringWriter s = new StringWriter();
        m.write( s, "RDF/XML-ABBREV" );
        Model m2 = modelWithStatements( "" );
        m2.read( new StringReader( s.toString() ), null, "RDF/XML" );
        assertIsoModels( m, m2 );
        //assertTrue( s.toString().contains( "]]&gt;" ) );  // Java 1.5-ism
        //assertFalse( s.toString().contains( "]]>" ) );
        assertTrue( s.toString().contains( "]]&gt;" ) );
        assertFalse( s.toString().contains( "]]>" ) );
        }
    }
