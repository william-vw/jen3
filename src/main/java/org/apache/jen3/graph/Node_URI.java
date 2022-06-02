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

import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.shared.*;

/**
    RDF nodes with a global identity given by a URI.
*/
public class Node_URI extends Node_Concrete
    {    
    /** @deprecated Use {@code Node_URI(String)} */
    @Deprecated
    protected Node_URI( Object uri )
        { super( uri ); }

    protected Node_URI( String uri )
        { super( uri ); }

    @Override
    public String getURI()
        { return (String) label; }
        
    @Override
    public Node visitWith( NodeVisitor v, Object data )
        { return v.visitURI( this, (String) label, data ); }
        
    @Override
	public NodeTypes getType() 
    	{ return NodeTypes.URI; }
    
    @Override
    public boolean isURI()
        { return true; }
        
    @Override
    public boolean isResource() 
    	{ return true; }
    
    /**
        Answer a String representing the node, taking into account the PrefixMapping.
        The horrible test against null is a stopgap to avoid a circularity issue.
        TODO fix the circularity issue
    */
    @Override
    public String toString( PrefixMapping pm, boolean quoting )
        { return pm == null ? (String) label : pm.shortForm( (String) label ); }
        
    @Override
    public boolean equals( Object other )
        {
        if ( this == other ) return true ;
        if (other instanceof Node_URI && same( (Node_URI) other ))
        	return true;
        else
        	return super.equals(other);
        }

    final boolean same( Node_URI other )
        { return label.equals( other.label ); }
    
    @Override
    public String getNameSpace()
        { 
        String s = (String) label;
        return s.substring( 0, TermUtil.splitNamespaceXML( s ) );
        }
    
    @Override
    public String getLocalName()
        {  
        String s = (String) label;
        return s.substring( TermUtil.splitNamespaceXML( s ) );
        }
    
    @Override
    public boolean hasURI( String uri )
        { return label.equals( uri ); }
    }
