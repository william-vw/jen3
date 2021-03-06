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

package org.apache.jen3.reasoner.rulesys.builtins;

import org.apache.jen3.graph.*;
import org.apache.jen3.reasoner.rulesys.*;
import org.apache.jen3.vocabulary.RDF;

/**
 * listEntry(?list, ?index, ?val) will bind ?val to the ?index'th entry
 * in the RDF list ?list. If there is no such entry the variable will be unbound
 * and the call will fail. Only useable in rule bodies.
 */
public class ListEntry extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    @Override
    public String getName() {
        return "listEntry";
    }
    
    /**
     * Return the expected number of arguments for this functor or 0 if the number is flexible.
     */
    @Override
    public int getArgLength() {
        return 3;
    }

    /**
     * This method is invoked when the builtin is called in a rule body.
     * @param args the array of argument values for the builtin, this is an array 
     * of Nodes, some of which may be Node_RuleVariables.
     * @param length the length of the argument list, may be less than the length of the args array
     * for some rule engines
     * @param context an execution context giving access to other relevant data
     * @return return true if the buildin predicate is deemed to have succeeded in
     * the current environment
     */
    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        Node list = getArg(0, args, context);
        Node index = getArg(1, args, context);
        if ( ! Util.isNumeric(index) )  return false;
        Node elt = getEntry(list, Util.getIntValue(index), context);
        if (elt == null) {
            return false;
        } else {
            env.bind(args[2], elt);
            return true;
        }
    }
    
    /**
     * Return the i'th element of the list, starting from 0
     * @param list the start of the list
     * @param i the element to return
     * @param context the context through which the data values can be found
     * @return the entry or null if the there isn't such an entry
     */
    protected static Node getEntry(Node list, int i, RuleContext context ) {
        int count = 0;
        Node node = list;
        while (node != null && !node.equals(RDF.Nodes.nil)) {
            if (count == i) {
                return Util.getPropValue(node, RDF.Nodes.first, context);
            } else {
                node = Util.getPropValue(node, RDF.Nodes.rest, context);
                count++;
            }
        }
        return null;
    }
    
}
