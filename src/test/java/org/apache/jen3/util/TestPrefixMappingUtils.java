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

package org.apache.jen3.util;

import java.io.StringReader ;

import junit.framework.JUnit4TestAdapter;

import org.apache.jen3.graph.Factory;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.shared.PrefixMapping;
import org.apache.jen3.shared.impl.PrefixMappingImpl;
import org.apache.jen3.util.PrefixMappingUtils;
import org.apache.jena.atlas.lib.StrUtils ;
import org.junit.Assert ;
import org.junit.Test ;

public class TestPrefixMappingUtils {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestPrefixMappingUtils.class) ;
    }

    static Graph create (String data) {
        Graph graph = Factory.createGraphMem() ;
        Model m = ModelFactory.createModelForGraph(graph);
        m.read(new StringReader(data), null, "TTL");
        return graph ;
    }
    
    static int size(PrefixMapping pmap) {
        return pmap.getNsPrefixMap().size() ;
    }
    
    @Test public void prefixes1() {
        // All prefixes used.
        String data1 = StrUtils.strjoinNL
            ("@prefix : <http://example/> ." ,
             "@prefix ex: <http://example/ex#> ." ,
             "" ,
             ":s1 :p :x1 ." ,
             ":s1 ex:p :x1 ."
             ) ;
        Graph graph1 = create(data1) ;
        PrefixMapping pmap = PrefixMappingUtils.calcInUsePrefixMapping(graph1) ;
        PrefixMapping pmapExpected = graph1.getPrefixMapping() ;
        Assert.assertEquals(2, size(pmap)) ;
        Assert.assertEquals(pmapExpected, pmap) ; 
    }
    
    @Test public void prefixes2() {
        // Some prefixes used
        String data2 = StrUtils.strjoinNL
            ("@prefix : <http://example/> ." ,
             "@prefix ex: <http://example/ex#> ." ,
             "@prefix notinuse: <http://example/whatever/> ." ,
             "" ,
             ":s1 :p :x1 ." ,
             ":s1 ex:p :x1 ."
             ) ;
        
        Graph graph1 = create(data2) ;
        PrefixMapping pmap = PrefixMappingUtils.calcInUsePrefixMapping(graph1) ;
        PrefixMapping pmapExpected = new PrefixMappingImpl() ;
        pmapExpected.setNsPrefix("", "http://example/") ;
        pmapExpected.setNsPrefix("ex", "http://example/ex#") ;
        Assert.assertEquals(2, size(pmap)) ;
        Assert.assertTrue(sameMapping(pmapExpected, pmap)) ;
        Assert.assertTrue(pmap.getNsPrefixURI("notinuse") == null) ;
    }

    @Test public void prefixes3() {
        // Some URIs without prefixes.
        String data = StrUtils.strjoinNL
            ("@prefix : <http://example/> ." ,
             "" ,
             "<http://other/s1> :p :x1 ."
             ) ;
        Graph graph1 = create(data) ;
        PrefixMapping pmap = PrefixMappingUtils.calcInUsePrefixMapping(graph1) ;
        PrefixMapping pmapExpected = new PrefixMappingImpl() ;
        pmapExpected.setNsPrefix("", "http://example/") ;
        Assert.assertTrue(sameMapping(pmapExpected, pmap)) ;
    }
    
    @Test public void prefixes4() {
        // No prefixes.
        String data = StrUtils.strjoinNL
            (
             "<http://other/s1> <http://example/p> 123 ."
             ) ;
        Graph graph1 = create(data) ;
        PrefixMapping pmap = PrefixMappingUtils.calcInUsePrefixMapping(graph1) ;
        Assert.assertEquals(0, size(pmap)) ;
        PrefixMapping pmapExpected = new PrefixMappingImpl() ;
        Assert.assertTrue(sameMapping(pmapExpected, pmap)) ;
    }
    
    @Test public void prefixesN() {
        // All combinations.
        String data = StrUtils.strjoinNL
            ("@prefix : <http://example/> ." ,
             "@prefix ex: <http://example/ex#> ." ,
             "@prefix notinuse: <http://example/whatever/> ." ,
             "@prefix indirect: <urn:foo:> ." ,
             "@prefix indirectx: <urn:x:> ." ,

             "@prefix ns: <http://host/ns> ." ,
             "@prefix ns1: <http://host/ns1> ." ,
             "@prefix ns2: <http://host/nspace> ." ,
             "" ,
             ":s1 :p :x1 ." ,
             ":s1 ex:p :x1 ." ,

             "<urn:foo:bar> :p 1 . ",
             "<urn:x:a:b> :p 2 . ",

             "<urn:verybad#.> :p 1 . ",

             "ns:x ns1:p 'ns1' . ",

             "<http://examp/abberev> indirect:p 'foo' . "
             ) ;
        
        Graph graph = create(data) ;
        PrefixMapping pmap = PrefixMappingUtils.calcInUsePrefixMapping(graph) ;
        PrefixMapping pmapExpected = new PrefixMappingImpl() ;
     	pmapExpected.setNsPrefix("", "http://example/") ;
		pmapExpected.setNsPrefix("ex", "http://example/ex#") ;
		pmapExpected.setNsPrefix("indirect", "urn:foo:") ;
		pmapExpected.setNsPrefix("ns", "http://host/ns") ;
		pmapExpected.setNsPrefix("ns1", "http://host/ns1") ;
		pmapExpected.setNsPrefix("indirectx", "urn:x:") ;
		//print("Expected:", pmapExpected) ;
		//print("Got:", pmap) ;
        Assert.assertTrue(sameMapping(pmapExpected, pmap)) ;
        Assert.assertTrue(pmap.getNsPrefixURI("notinuse") == null) ;
    }

    private boolean sameMapping(PrefixMapping pmapExpected, PrefixMapping pmap) {
        return pmapExpected.samePrefixMappingAs(pmap) ;
    }
    
    private static void print(String label, PrefixMapping pmap) {
        System.out.println(label);
        pmap.getNsPrefixMap().forEach((p,u)->System.out.printf("    %s: -> <%s>\n", p,u)) ;
    }
}
