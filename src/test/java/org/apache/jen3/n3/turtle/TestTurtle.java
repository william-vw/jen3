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

package org.apache.jen3.n3.turtle;


import org.apache.jen3.n3.turtle.TurtleParseException;
import org.apache.jen3.n3.turtle.TurtleReader;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.RDFReader;
import org.apache.jen3.util.FileManager;
import org.apache.jen3.util.FileUtils;

import junit.framework.TestCase;


public class TestTurtle extends TestCase
{
    String input ;
    String output ;
    String baseIRI ;
    
    public TestTurtle(String name, String input, String output, String baseIRI)
    { super(name) ; this.input = input ; this.output = output ; this.baseIRI = baseIRI ; }
    
    @Override
    public void runTest()
    {
        Model model = ModelFactory.createDefaultModel() ;
        RDFReader t = new TurtleReader() ;
        try {
            if ( baseIRI != null )
                t.read(model, FileManager.get().open(input), baseIRI) ;
            else
                t.read(model, input) ;  
            // "http://www.w3.org/2001/sw/DataAccess/df1/tests/rdfq-results.ttl"

            String syntax = FileUtils.guessLang(output, FileUtils.langNTriple) ;
            
            Model results = FileManager.get().loadModel(output, syntax);
            boolean b = model.isIsomorphicWith(results) ;
            if ( !b )
                assertTrue("Models not isomorphic", b) ;
        } catch (TurtleParseException ex)
        {
            throw ex ;    
        }
    }
}
