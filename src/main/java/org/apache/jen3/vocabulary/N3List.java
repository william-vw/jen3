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
 * The N3 list namespace.
 * 
 * 
 * @author wvw
 *
 */

public class N3List {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "http://www.w3.org/2000/10/swap/list#";

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

	public static final Resource length = resource("length");
	public static final Resource member = resource("member");
	public static final Resource memberAt = resource("memberAt");
	public static final Resource in = resource("in");
}
