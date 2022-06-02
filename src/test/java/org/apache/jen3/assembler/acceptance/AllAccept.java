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

package org.apache.jen3.assembler.acceptance;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;

import junit.framework.TestSuite ;

import org.apache.jen3.assembler.Assembler;
import org.apache.jen3.assembler.test.AssemblerTestBase;
import org.apache.jen3.rdf.model.InfModel;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jen3.util.FileUtils;

public class AllAccept extends AssemblerTestBase
    {
    public AllAccept( String name )
        { super( name ); }
    
    public static TestSuite suite()
        {
        TestSuite result = new TestSuite();
        result.addTestSuite( AllAccept.class );
        return result;
        }
    
    public void testUnadornedInferenceModel()
        {
        Resource root = resourceInModel( "x ja:reasoner R; R rdf:type ja:ReasonerFactory" );
        Model m = Assembler.general.openModel( root );
        assertInstanceOf( InfModel.class, m );
        InfModel inf = (InfModel) m;
        assertIsoModels( empty, inf.getRawModel() );
        assertInstanceOf( GenericRuleReasoner.class, inf.getReasoner() );
        }
    
    public void testWithContent() throws IOException
        {
        File f = FileUtils.tempFileName( "assembler-acceptance-", ".n3" );
        Model data = model( "a P b; b Q c" );
        try ( FileOutputStream fs = new FileOutputStream( f ) ) {
            data.write( fs, "N3" );
        }
        Resource root = resourceInModel( "x rdf:type ja:MemoryModel; x ja:content y; y ja:externalContent file:" + f.getAbsolutePath() );
        Model m = Assembler.general.openModel( root );
        assertIsoModels( data, m );
        }
    }
