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

package org.apache.jen3.assembler.exceptions;

import org.apache.jen3.rdf.model.*;

/**
    Exception used to report that a required property is missing.
*/
public class PropertyRequiredException extends AssemblerException
    {
    protected final Property property;
    
    public PropertyRequiredException( Resource root, Property property )
        {
        super( root, makeMessage( root, property ) );
        this.property = property;
        }
    
    private static String makeMessage( Resource root, Property property )
        {
        return
            "the object " + nice( root ) 
            + " could not be constructed because it is missing the required property " + nice( property )
            ;
        }
    
    /**
        Answer the required property.
    */
    public Resource getProperty()
        { return property; }
    }
