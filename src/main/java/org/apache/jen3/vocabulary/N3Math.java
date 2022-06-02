package org.apache.jen3.vocabulary;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceFactory;

public class N3Math {


	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "http://www.w3.org/2000/10/swap/math#";

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

	public static final Resource absoluteValue = resource("absoluteValue");
	public static final Resource lessThan = resource("lessThan");
	public static final Resource lessThanOrEqual = resource("lessThanOrEqual");
	public static final Resource equalTo = resource("equalTo");
	public static final Resource greaterThanOrEqual = resource("greaterThanOrEqual");
	public static final Resource greaterThan = resource("greaterThan");
	public static final Resource notGreaterThan = resource("notGreaterThan");
	public static final Resource notLessThan = resource("notLessThan");
	public static final Resource notEqualTo = resource("notEqualTo");
}
