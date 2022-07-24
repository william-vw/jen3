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

package org.apache.jen3.vocabulary;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceFactory;

/**
 * The N3 log namespace.
 * 
 * 
 * @author wvw
 *
 */

public class N3Log {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "http://www.w3.org/2000/10/swap/log#";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	protected static final Resource resource(String local) {
		return ResourceFactory.createResource(uri + local);
	}

	public static final Resource implies = resource("implies");
	public static final Resource impliedBy = resource("impliedBy");
	public static final Resource equalTo = resource("equalTo");
	public static final Resource notEqualTo = resource("notEqualTo");
	public static final Resource becomes = resource("becomes");
	public static final Resource comesFrom = resource("comesFrom");
	public static final Resource content = resource("content");
	public static final Resource semantics = resource("semantics");
	public static final Resource outputString = resource("outputString");
	public static final Resource Uri = resource("Uri");
	public static final Resource BlankNode = resource("BlankNode");
	public static final Resource Formula = resource("Formula");
	public static final Resource Literal = resource("Literal");
	public static final Resource List = resource("List");
	public static final Resource Set = resource("Set");
	public static final Resource Other = resource("Other");
	public static final Resource StableTruth = resource("StableTruth");
}
