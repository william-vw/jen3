package org.apache.jen3.vocabulary;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceFactory;

/**
 * The N3 reason namespace.
 * 
 * 
 * @author wvw
 *
 */

public class N3Reason {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "http://www.w3.org/2000/10/swap/reason#";

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
}
