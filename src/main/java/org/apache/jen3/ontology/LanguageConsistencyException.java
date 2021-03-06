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

// Package
///////////////
package org.apache.jen3.ontology;


// Imports
///////////////

/**
 * <p>
 * An exception that denotes some inconsistency between the arguments to a method
 * in the ontology API, and the language definition for a given ontology profile.
 * For example, the arguments of an intersection class description should all be classes.
 * </p>
 */
public class LanguageConsistencyException
    extends OntologyException
{
    // Constants
    //////////////////////////////////
    private static final long serialVersionUID = 1962961408291647160L;


    // Static variables
    //////////////////////////////////

    // Instance variables
    //////////////////////////////////

    // Constructors
    //////////////////////////////////

    public LanguageConsistencyException( String message ) {
        super( message );
    }

    // External signature methods
    //////////////////////////////////

    // Internal implementation methods
    //////////////////////////////////

    //==============================================================================
    // Inner class definitions
    //==============================================================================

}
