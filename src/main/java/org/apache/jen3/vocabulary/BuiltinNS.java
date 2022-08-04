package org.apache.jen3.vocabulary;

import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.ResourceFactory;

public class BuiltinNS {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri = "http://www.w3.org/2000/10/swap/builtin#";

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

	public static final Resource BuiltinDef = resource("BuiltinDefinition");
	public static final Resource AtomicIC = resource("AtomicInputConstraint");
	public static final Resource ConcreteIC = resource("ConcreteInputConstraint");
	public static final Resource VariableIC = resource("VariableInputConstraint");
	public static final Resource IriIC = resource("IriInputConstraint");
	public static final Resource BnodeIC = resource("BnodeInputConstraint");
	public static final Resource NumberableIC = resource("NumberableInputConstraint");
	public static final Resource IntableIC = resource("IntableInputConstraint");
	public static final Resource DatatypeIC = resource("DatatypeInputConstraint");
	public static final Resource LiteralIC = resource("LiteralInputConstraint");
	public static final Resource BaseIriIC = resource("BaseIriInputConstraint");
	public static final Resource ListIC = resource("ListInputConstraint");
	public static final Resource UnionIC = resource("UnionInputConstraint");
	public static final Resource StringIC = resource("StringInputConstraint");
	public static final Resource StringableIC = resource("StringableInputConstraint");
	public static final Resource RegexIC = resource("RegexInputConstraint");
	public static final Resource FormulaIC = resource("FormulaInputConstraint");
	public static final Resource AnyIC = resource("AnyInputConstraint");

	public static final Resource inputConstraints = resource("inputConstraints");
	public static final Resource subject = resource("subject");
	public static final Resource object = resource("object");
	public static final Resource subjectObject = resource("subjectObject");
	public static final Resource restricts = resource("restricts");
	public static final Resource datatype = resource("datatype");
	public static final Resource oneOf = resource("oneOf");
	public static final Resource size = resource("size");
	public static final Resource cardinality = resource("cardinality");
	public static final Resource listElementType = resource("listElementType");
	public static final Resource listElements = resource("listElements");
	public static final Resource accept = resource("accept");
	public static final Resource domain = resource("domain");
	public static final Resource modeSetter = resource("modeSetter");
	public static final Resource notBound = resource("notBound");
	public static final Resource isResourceIntensive = resource("isResourceIntensive");
	public static final Resource isUniversal = resource("isUniversal");
	public static final Resource instantiate = resource("instantiate");
	public static final Resource isStatic = resource("isStatic");
//	public static final Resource isLowPriority = resource("isLowPriority");

	public static final Resource Exact = resource("Exact");
	public static final Resource Min = resource("Min");
	public static final Resource Max = resource("Max");
	public static final Resource Other = resource("Other");
	public static final Resource DomainMatch = resource("DomainMatch");

	public static final Resource ResolveJavaClass = resource("ResolveJavaClass");
	public static final Resource javaPackage = resource("javaPackage");
}
