/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.ext.xerces.impl.dv.xs;

import org.apache.jen3.ext.xerces.impl.dv.XSSimpleType;
import org.apache.jen3.ext.xerces.util.SymbolHash;

/**
 * the factory to create/return built-in schema 1.0 DVs and create user-defined DVs
 * 
 * {@literal @xerces.internal} 
 *
 * @author Neeraj Bajaj, Sun Microsystems, inc.
 * @author Sandy Gao, IBM
 * @author Khaled Noaman, IBM
 *
 * @version $Id: SchemaDVFactoryImpl.java 710089 2008-11-03 16:01:16Z knoaman $
 */
public class SchemaDVFactoryImpl extends BaseSchemaDVFactory {

    static final SymbolHash fBuiltInTypes = new SymbolHash();
    
    static {
        createBuiltInTypes();
    }

    // create all built-in types
    static void createBuiltInTypes() {
    	createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType);
    	
        // TODO: move specific 1.0 DV implementation from base
    } //createBuiltInTypes()

    /**
     * Get a built-in simple type of the given name
     * REVISIT: its still not decided within the Schema WG how to define the
     *          ur-types and if all simple types should be derived from a
     *          complex type, so as of now we ignore the fact that anySimpleType
     *          is derived from anyType, and pass 'null' as the base of
     *          anySimpleType. It needs to be changed as per the decision taken.
     *
     * @param name  the name of the datatype
     * @return      the datatype validator of the given name
     */
    @Override
    public XSSimpleType getBuiltInType(String name) {
        return (XSSimpleType)fBuiltInTypes.get(name);
    }

    /**
     * get all built-in simple types, which are stored in a hashtable keyed by
     * the name
     *
     * @return      a hashtable which contains all built-in simple types
     */
    @Override
    public SymbolHash getBuiltInTypes() {
        return fBuiltInTypes.makeClone();
    }

}//SchemaDVFactoryImpl
