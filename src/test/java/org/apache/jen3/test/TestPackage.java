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

package org.apache.jen3.test;

import org.apache.jen3.assembler.test.TestAssemblerPackage;

import junit.framework.TestCase ;
import junit.framework.TestSuite ;

/**
 * All developers should edit this file to add their tests.
 * Please try to name your tests and test suites appropriately.
 * Note, it is better to name your test suites on creation
 * rather than in this file.
 */
public class TestPackage extends TestCase {

    static {
        // Running directly (e.g. Eclipse) not from the POM. 
        System.setProperty("log4j.configuration", JenaTest.log4jFilenameTests);
    }
	
    static public TestSuite suite() {
        TestSuite ts = new TestSuite() ;
        ts.setName("Jena") ;
        addTest(ts,  "System setup", TestSystemSetup.suite());
        addTest(ts,  "Enhanced", org.apache.jen3.enhanced.test.TestPackage.suite());
        addTest(ts,  "Datatypes", org.apache.jen3.datatypes.TestPackage.suite()) ;
        addTest(ts,  "Graph", org.apache.jen3.graph.test.TestPackage.suite());
        addTest(ts,  "Mem", org.apache.jen3.mem.test.TestMemPackage.suite() );
        addTest(ts,  "Mem2", org.apache.jen3.mem.test.TestGraphMemPackage.suite() );
        addTest(ts,  "Model", org.apache.jen3.rdf.model.test.TestPackage.suite());
        // addTest(ts,  "N3", org.apache.jena.n3.N3TestSuite.suite());
        addTest(ts,  "Turtle", org.apache.jen3.n3.turtle.TurtleTestSuite.suite()) ;
        addTest(ts,  "XML Output", org.apache.jen3.rdfxml.xmloutput.TestPackage.suite());
        addTest(ts,  "Util", org.apache.jen3.util.TestPackage.suite());
        addTest(ts,  "Jena iterator", org.apache.jen3.util.iterator.test.TestPackage.suite() );
        addTest(ts,  "Assembler", TestAssemblerPackage.suite() );
        addTest(ts,  "ARP", org.apache.jen3.rdfxml.xmlinput.TestPackage.suite());
        addTest(ts,  "Vocabularies", org.apache.jen3.vocabulary.test.TestVocabularies.suite() );
        addTest(ts,  "Shared", org.apache.jen3.shared.TestSharedPackage.suite() );
        addTest(ts,  "Reasoners", org.apache.jen3.reasoner.test.TestPackage.suite());
        addTest(ts,  "Composed graphs", org.apache.jen3.graph.compose.test.TestPackage.suite() );
        addTest(ts,  "Ontology", org.apache.jen3.ontology.impl.TestPackage.suite() );
        return ts ;
    }

    private static void addTest(TestSuite ts, String name, TestSuite tc) {
        if ( name != null )
            tc.setName(name);
        ts.addTest(tc);
    }

}
