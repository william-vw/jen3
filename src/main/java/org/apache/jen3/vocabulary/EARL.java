package org.apache.jen3.vocabulary;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceFactory;

public class EARL {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "https://www.w3.org/TR/EARL10-Schema/#";

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
	
	public static final Resource Assertion = resource("Assertion");
	public static final Resource Software = resource("Software");
	public static final Resource Fail = resource("Fail");
	public static final Resource Pass = resource("Pass");
	public static final Resource assertedBy = resource("assertedBy");
	public static final Resource subject = resource("subject");
	public static final Resource test = resource("test");
	public static final Resource result = resource("result");
	public static final Resource outcome = resource("outcome");
}
