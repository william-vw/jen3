package org.apache.jen3.vocabulary;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceFactory;

/**
 * The N3 test namespace.
 * 
 * 
 * @author wvw
 *
 */

public class N3Test {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "https://w3c.github.io/N3/tests/test.n3#";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	public static final Resource resource(String local) {
		return ResourceFactory.createResource(uri + local);
	}

	public static final Resource TestN3 = resource("TestN3");
	public static final Resource TestN3Reason = resource("TestN3Reason");
	public static final Resource N3ReasonFailed = resource("N3ReasonFailed");
	public static final Resource N3ReasonPass = resource("N3ReasonPass");
	
	public static final Resource TestN3Positive = resource("TestN3Positive");
	public static final Resource TestN3Negative = resource("TestN3Negative");
	public static final Resource TestN3Syntax = resource("TestN3Syntax");
	public static final Resource TestN3PositiveSyntax = resource("TestN3PositiveSyntax");
	public static final Resource TestN3NegativeSyntax = resource("TestN3NegativeSyntax");
	public static final Resource N3SyntaxFailed = resource("N3SyntaxFailed");
	public static final Resource N3SyntaxPass = resource("N3SyntaxPass");

	public static final Resource out = resource("out");
	public static final Resource output = resource("output");
}
