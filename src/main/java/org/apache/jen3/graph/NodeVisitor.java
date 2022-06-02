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

package org.apache.jen3.graph;

import org.apache.jen3.graph.impl.LiteralLabel;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.graph.n3.Node_QuantifiedVariable;
import org.apache.jen3.graph.n3.Node_Quantifier;
import org.apache.jen3.graph.n3.Node_QuickVariable;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;

/**
    The NodeVisitor interface is used by Node::visitWith so that an application
    can have type-dispatch on the class of a Node. 	
*/
public interface NodeVisitor
    {
    Node visitAny( Node_ANY it, Object data );
    Node visitBlank( Node_Blank it, BlankNodeId id, Object data );
    Node visitLiteral( Node_Literal it, LiteralLabel lit, Object data );
    Node visitURI( Node_URI it, String uri, Object data );
    Node visitVariable( Node_Variable it, String name, Object data );
    Node visitRuleVariable( Node_RuleVariable it, Object data );
    Node visitQuickVariable( Node_QuickVariable it, Object data );
    Node visitQuantifiedVariable( Node_QuantifiedVariable it, Object data );
    Node visitQuantifier( Node_Quantifier it, Object data );
    Node visitCollection( Node_Collection it, Object data );
    Node visitCitedFormula( Node_CitedFormula it, Object data );
    }
