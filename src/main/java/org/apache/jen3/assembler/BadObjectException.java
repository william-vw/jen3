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

package org.apache.jen3.assembler;

import org.apache.jen3.assembler.exceptions.AssemblerException;
import org.apache.jen3.rdf.model.*;

/**
    Exception used to report that the object of a statement is not a Resource.
    The subject of the exception is treated as the root object. The nature of the
    unsuitability is (currently) not described.
*/
public class BadObjectException extends AssemblerException
    {
    protected final RDFObject object;
    
    public BadObjectException( Statement s )
        { 
        super( s.getSubject(), makeMessage( s ) ); 
        this.object = s.getObject();
        }

    private static String makeMessage( Statement s )
        { 
        RDFObject subject = s.getObject();
        return 
            "the " + typeOf( subject ) + " " + nice( subject ) 
            + " is unsuitable as the object of a " + nice( s.getPredicateProperty() ) 
            + " statement";
        }

    private static String typeOf( RDFObject x )
        { return x.isAnon() ? "bnode" : x.isLiteral() ? "literal" : "resource"; }

    public RDFObject getObject()
        { return object; }
    }
