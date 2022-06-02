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

package org.apache.jen3.reasoner.rulesys;

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jen3.datatypes.DatatypeFormatException;
import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.datatypes.xsd.XSDDateTime;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.impl.LiteralLabelFactory;
import org.apache.jen3.graph.n3.Node_CitedFormula;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.n3.ParserN3;
import org.apache.jen3.n3.impl.N3ModelImpl.N3GraphConfig;
import org.apache.jen3.rdf.model.Literal;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.Property;
import org.apache.jen3.rdf.model.RDFObject;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.reasoner.Finder;
import org.apache.jen3.reasoner.IllegalParameterException;
import org.apache.jen3.reasoner.TriplePattern;
import org.apache.jen3.reasoner.rulesys.MathOp.MathOperations;
import org.apache.jen3.util.FileUtils;
import org.apache.jen3.util.iterator.ClosableIterator;
import org.apache.jen3.vocabulary.RDF;

/**
 * A small random collection of utility functions used by the rule systems.
 */
public class Util {

	private static String filePrefix = "file://";

	/**
	 * Return the integer value of a literal node
	 */
	public static int getIntValue(Node n) {
		return ((Number) n.getLiteralValue()).intValue();
	}

	/**
	 * Return the double value of a literal node
	 */
	public static double getDoubleValue(Node n) {
		return ((Number) n.getLiteralValue()).doubleValue();
	}

	/**
	 * Check whether a Node is an Instant (DateTime) value
	 */
	public static boolean isInstant(Node n) {
		if (n.isLiteral()) {
			Object o = n.getLiteralValue();
			return (o instanceof XSDDateTime);
		} else {
			return false;
		}
	}

	public static boolean isBoolean(Node n) {
		return n.isLiteral() && n.getLiteralDatatype().equals(XSDDatatype.XSDboolean);
	}

	public static boolean getBooleanValue(Node n) {
		return ((Boolean) n.getLiteralValue()).booleanValue();
	}

	public static N3Model getModelValue(Node n) {
		return ((Node_CitedFormula) n).getContents();
	}

	/**
	 * Test if two literals are comparable by an order operator (both numbers or
	 * both times)
	 */
	public static boolean comparable(Node n1, Node n2) {
		return (isNumeric(n1) && isNumeric(n2)) || (isInstant(n1) && isInstant(n2));
	}

	/**
	 * Compare two numeric nodes.
	 * 
	 * @param n1 the first numeric valued literal node
	 * @param n2 the second numeric valued literal node
	 * @return -1 if n1 is less than n2, 0 if n1 equals n2 and +1 if n1 greater than
	 *         n2
	 * @throws ClassCastException if either node is not numeric
	 */
	public static int compareNumbers(Node n1, Node n2) {
		if (n1.isLiteral() && n2.isLiteral()) {
			Object v1 = n1.getLiteralValue();
			Object v2 = n2.getLiteralValue();
			if (v1 instanceof Number && v2 instanceof Number) {
				Number num1 = (Number) v1;
				Number num2 = (Number) v2;
				return compareNumbers(num1, num2);
			}
		}
		throw new ClassCastException("Non-numeric literal in compareNumbers");
	}

	public static int compareNumbers(Number num1, Number num2) {
		// Wrapped primitives, with integer values.
		if (valueIsLong(num1) && valueIsLong(num2)) {
			long z1 = num1.longValue();
			long z2 = num2.longValue();
			return Long.compare(z1, z2);
		}
		// Wrapped primitives, with floating point values.
		if (valueIsDouble(num1) && valueIsDouble(num2)) {
			double d1 = num1.doubleValue();
			double d2 = num2.doubleValue();
			return compare(d1, d2);
		}
		// Both BigDecimal
		if (num1 instanceof BigDecimal && num2 instanceof BigDecimal) {
			BigDecimal dec1 = (BigDecimal) num1;
			BigDecimal dec2 = (BigDecimal) num2;
			return dec1.compareTo(dec2);
		}

		Double d1 = convertToDouble(num1);
		Double d2 = convertToDouble(num2);
		return compare(d1, d2);
	}

	public static int compare(double d1, double d2) {
		if (Double.isNaN(d1) || Double.isNaN(d2))
			return -1;
		return Double.compare(d1, d2);
	}

	private static int noOpPrec = 2; // integer
	private static List<Class<?>> numCls = Arrays.asList(Byte.class, Short.class, Integer.class, Long.class,
			BigDecimal.class, Float.class, Double.class);

	public static int getPrecision(Node node) {
		return numCls.indexOf(node.getLiteralValue().getClass());
	}

	public static int getMaxPrecision(List<Node> nodes) {
		if (nodes.isEmpty())
			return noOpPrec;
		else
			return nodes.stream().mapToInt(n -> getPrecision(n)).max().getAsInt();
	}

	public static Object sum(Node... nodes) {
		return sum(Arrays.asList(nodes));
	}

	public static Number sum(List<Node> nodes) {
		return applyOp(nodes, MathOperations.ADD);
	}

	public static Number difference(Node... nodes) {
		return difference(Arrays.asList(nodes));
	}

	public static Number difference(List<Node> nodes) {
		return applyOp(nodes, MathOperations.SUBTRACT);
	}

	// TODO
	// what if result exceeds range of "max" precision?

	public static Object product(Node... nodes) {
		return product(Arrays.asList(nodes));
	}

	public static Number product(List<Node> nodes) {
		return applyOp(nodes, MathOperations.MULTIPLY);
	}

	public static Object quotient(Node... nodes) {
		return quotient(Arrays.asList(nodes));
	}

	// NOTE use double precision in calculation

	public static Number quotient(List<Node> nodes) {
		return new BigDecimal(applyOp(nodes, MathOperations.DIVIDE).doubleValue());
	}

	public static Object remainder(Node... nodes) {
		return remainder(Arrays.asList(nodes));
	}

	// NOTE use integer precision in calculation

	public static Number remainder(List<Node> nodes) {
		return applyOp(nodes, MathOperations.REMAINDER);
	}

	// TODO
	// what if result exceeds range of "max" precision?

	public static Object exponentiation(Node... nodes) {
		return exponentiation(Arrays.asList(nodes));
	}

	public static Number exponentiation(List<Node> nodes) {
		return applyOp(nodes, MathOperations.EXPONENTIATION);
	}

	// NOTE use BigDecimal precision in calculation

	public static Object logarithm(Node... nodes) {
		return logarithm(Arrays.asList(nodes));
	}

	public static Number logarithm(List<Node> nodes) {
		return applyOp(nodes, MathOperations.LOGARITHM);
	}

	public static Number applyOp(MathOperations op, Node... nodes) {
		return applyOp(Arrays.asList(nodes), op);
	}

	public static Number applyOp(List<Node> nodes, MathOperations op) {
		return applyOp(nodes, op.getMaxPrecision(nodes), op);
	}

	private static Number applyOp(List<Node> nodes, int precision, MathOperations op) {
		if (nodes.isEmpty())
			return op.getNoOpValue();

		switch (precision) {

		case 0:
			byte bres = parseNumber(nodes.get(0)).byteValue();
			for (int i = 1; i < nodes.size(); i++)
				bres = MathOp.ByteOp.apply(op, bres, parseNumber(nodes.get(i)).byteValue());
			return bres;

		case 1:
			short sres = parseNumber(nodes.get(0)).shortValue();
			for (int i = 1; i < nodes.size(); i++)
				sres = MathOp.ShortOp.apply(op, sres, parseNumber(nodes.get(i)).shortValue());
			return sres;

		case 2:
			int ires = parseNumber(nodes.get(0)).intValue();
			for (int i = 1; i < nodes.size(); i++)
				ires = MathOp.IntOp.apply(op, ires, parseNumber(nodes.get(i)).intValue());
			return ires;

		case 3:
			long lres = parseNumber(nodes.get(0)).longValue();
			for (int i = 1; i < nodes.size(); i++)
				lres = MathOp.LongOp.apply(op, lres, parseNumber(nodes.get(i)).longValue());
			return lres;

		case 4:
			BigDecimal bd = new BigDecimal(parseNumber(nodes.get(0)).doubleValue());
			for (int i = 1; i < nodes.size(); i++)
				bd = MathOp.BigDecimalOp.apply(op, bd, new BigDecimal(parseNumber(nodes.get(i)).doubleValue()));
			return bd;

		case 5:
			float fres = parseNumber(nodes.get(0)).floatValue();
			for (int i = 1; i < nodes.size(); i++)
				fres = MathOp.FloatOp.apply(op, fres, parseNumber(nodes.get(i)).floatValue());
			return fres;

		case 6:
			double dres = parseNumber(nodes.get(0)).doubleValue();
			for (int i = 1; i < nodes.size(); i++)
				dres = MathOp.DoubleOp.apply(op, dres, parseNumber(nodes.get(i)).doubleValue());
			return dres;
		}

		return -1;
	}

	public static Number negation(Node node) {
		return applyOp(node, MathOperations.NEGATION);
	}

	public static Number absoluteValue(Node node) {
		return applyOp(node, MathOperations.ABSOLUTE_VALUE);
	}

	// NOTE returns integer result

	public static Number rounded(Node node) {
		return applyOp(node, MathOperations.ROUNDED).intValue();
	}

	public static Number rounded(Node n1, Node n2) {
//		if (valueIsDouble(nr)) {
//			double factor = Math.pow(10, nrDigits);
//			return Math.round(nr.doubleValue() * factor) / factor;
//
//		} else if (nr instanceof BigDecimal)
//			return ((BigDecimal) nr).round(new MathContext(nrDigits + 1));
//		else
//			return nr;

		return applyOp(MathOperations.ROUNDED, n1, n2);
	}

	public static Number ceiling(Node node) {
		return applyOp(node, MathOperations.CEILING).intValue();
	}

	public static Number floor(Node node) {
		return applyOp(node, MathOperations.FLOOR).intValue();
	}

	private static Number applyOp(Node node, MathOperations op) {
		return applyOp(node, op.getPrecision(node), op);
	}

	private static Number applyOp(Node node, int precision, MathOperations op) {
		switch (precision) {

		case 0:
			return MathOp.ByteOp.apply(op, parseNumber(node).byteValue());

		case 1:
			return MathOp.ShortOp.apply(op, parseNumber(node).shortValue());

		case 2:
			return MathOp.IntOp.apply(op, parseNumber(node).intValue());

		case 3:
			return MathOp.LongOp.apply(op, parseNumber(node).longValue());

		case 4:
			return MathOp.BigDecimalOp.apply(op, new BigDecimal(parseNumber(node).doubleValue()));

		case 5:
			return MathOp.FloatOp.apply(op, parseNumber(node).floatValue());

		case 6:
			return MathOp.DoubleOp.apply(op, parseNumber(node).doubleValue());
		}

		return -1;
	}

	/**
	 * Check whether a Node is a numeric (integer) value
	 */
	private static Pattern intPat = Pattern.compile("[+-]?[0-9]+");
	private static Pattern decPat = Pattern.compile("[+-]?[0-9]*\\.[0-9]+");
	private static Pattern doublePat = Pattern
			.compile("[+-]?([0-9]+\\.[0-9]*[eE][+-]?[0-9]+|\\.[0-9]+[eE][+-]?[0-9]+|[0-9]+[eE][+-]?[0-9]+)");
	private static List<Pattern> numericPats = Arrays.asList(intPat, decPat, doublePat);

	public static boolean isNumeric(Node n) {
		if (n.isLiteral()) {
			if (n.getLiteralValue() instanceof Number)
				return true;

			String str = n.getLiteralValue().toString();
			return numericPats.stream().anyMatch(p -> p.matcher(str).matches());

		} else
			return false;
	}

	public static Number parseNumber(Node node) {
		Number ret = null;

		if (node.getLiteralValue() instanceof Number)
			ret = ((Number) node.getLiteralValue());
		else {
			String str = node.getLiteralValue().toString();
			if (intPat.matcher(str).matches())
				ret = Integer.parseInt(str);
			else if (decPat.matcher(str).matches())
				ret = new BigDecimal(str);
			else if (doublePat.matcher(str).matches())
				ret = Double.parseDouble(str);
		}

		return ret;
	}

	public static boolean isInt(Node n) {
		if (n.isLiteral()) {
			if (n.getLiteralValue() instanceof Number)
				return valueIsLong((Number) n.getLiteralValue());

			String str = n.getLiteralValue().toString();
			return intPat.matcher(str).matches();

		} else
			return false;
	}

	public static int parseInt(Node node) {
		if (node.getLiteralValue() instanceof Number)
			return ((Number) node.getLiteralValue()).intValue();
		else {
			String str = node.getLiteralValue().toString();
			return Integer.parseInt(str);
		}
	}

	/**
	 * Check whether a Node is a string-able value
	 */
	public static boolean isStringable(Node n) {
		return n.isLiteral() || n.isURI();
	}

	public static String parseString(Node n) {
		switch (n.getType()) {

		case URI:
			return n.getURI();

		// TODO tie in N3JenaWriter[..] class
		case CITED_FORMULA:
		case COLLECTION:
			return "";

		case BLANK:
			return n.getBlankNodeLabel();

		case LITERAL:
			return n.getLiteralValue().toString();

		// TODO
		default:
			if (n.isVariable())
				return n.getName();

			return "";
		}
	}

	public static Object parseLiteral(Node n, RDFDatatype datatype) {
		if (n.isLiteral()) {
			String lex = n.getLiteralLexicalForm();

			try {
				return datatype.parse(lex);

			} catch (DatatypeFormatException e) {
				return null;
			}
		}

		return null;
	}

//	private static BigDecimal convertToBigDecimal(Number num) {
//		if (valueIsLong(num))
//			return new BigDecimal(num.longValue());
//		if (num instanceof BigDecimal)
//			return (BigDecimal) num;
//		// double and float.
//		return new BigDecimal(num.doubleValue());
//	}

	private static double convertToDouble(Number num) {
		return num.doubleValue();
	}

	public static boolean isLongType(Node n) {
		if (n.isLiteral()) {
			if (n.getLiteralValue() instanceof Number)
				return valueIsLong((Number) n.getLiteralValue());
		}

		return false;
	}

	private static boolean valueIsLong(Number v) {
		if (v instanceof Long)
			return true;
		if (v instanceof Integer)
			return true;
		if (v instanceof Short)
			return true;
		if (v instanceof Byte)
			return true;
		return false;
	}

	private static boolean valueIsDouble(Number v) {
		if (v instanceof Double)
			return true;
		if (v instanceof Float)
			return true;
		return false;
	}

	public static Node createNumeral(Number number) {
		return NodeFactory.createLiteralByValue(number, XSDDatatype.getFromCls(number.getClass()));
	}

	/**
	 * Compare two time Instant nodes.
	 * 
	 * @param n1 the first time instant (XSDDateTime) valued literal node
	 * @param n2 the second time instant (XSDDateTime) valued literal node
	 * @return -1 if n1 is less than n2, 0 if n1 equals n2 and +1 if n1 greater than
	 *         n2
	 * @throws ClassCastException if either not is not numeric
	 */
	public static int compareInstants(Node n1, Node n2) {
		if (n1.isLiteral() && n2.isLiteral()) {
			Object v1 = n1.getLiteralValue();
			Object v2 = n2.getLiteralValue();
			if (v1 instanceof XSDDateTime && v2 instanceof XSDDateTime) {
				XSDDateTime a = (XSDDateTime) v1;
				XSDDateTime b = (XSDDateTime) v2;
				return a.compare(b);
			}
		}
		throw new ClassCastException("Non-numeric literal in compareNumbers");
	}

	/**
	 * General order comparator for typed literal nodes, works for all numbers and
	 * for date times.
	 */
	public static int compareTypedLiterals(Node n1, Node n2) {
		if (n1.isLiteral() && n2.isLiteral()) {
			Object v1 = n1.getLiteralValue();
			Object v2 = n2.getLiteralValue();
			if (v1 instanceof Number && v2 instanceof Number) {
				return compareNumbers((Number) v1, (Number) v2);
			}
			if (v1 instanceof XSDDateTime && v2 instanceof XSDDateTime) {
				XSDDateTime a = (XSDDateTime) v1;
				XSDDateTime b = (XSDDateTime) v2;
				return a.compare(b);
			}
		}
		throw new ClassCastException("Compare typed literals can only compare numbers and datetimes");
	}

	/**
	 * Helper - returns the (singleton) value for the given property on the given
	 * root node in the data graph.
	 */
	public static Node getPropValue(Node root, Node prop, Finder context) {
		return doGetPropValue(context.find(new TriplePattern(root, prop, null)));
	}

	/**
	 * Helper - returns the (singleton) value for the given property on the given
	 * root node in the data graph.
	 */
	public static Node getPropValue(Node root, Node prop, Graph context) {
		return doGetPropValue(context.find(root, prop, null));
	}

	/**
	 * Helper - returns the (singleton) value for the given property on the given
	 * root node in the data graph.
	 */
	public static Node getPropValue(Node root, Node prop, RuleContext context) {
		return doGetPropValue(context.find(root, prop, null));
	}

	/**
	 * Internal implementation of all the getPropValue variants.
	 */
	private static Node doGetPropValue(ClosableIterator<Triple> it) {
		Node result = null;
		if (it.hasNext()) {
			result = it.next().getObject();
		}
		it.close();
		return result;
	}

	/**
	 * Convert an (assumed well formed) RDF list to a java list of Nodes
	 * 
	 * @param root    the root node of the list
	 * @param context the graph containing the list assertions
	 */
	public static List<Node> convertList(Node root, RuleContext context) {
		return convertList(root, context, new LinkedList<Node>());
	}

	/**
	 * Convert an (assumed well formed) RDF list to a java list of Nodes
	 */
	private static List<Node> convertList(Node node, RuleContext context, List<Node> sofar) {
		if (node == null || node.equals(RDF.nil.asNode()))
			return sofar;
		Node next = getPropValue(node, RDF.first.asNode(), context);
		if (next != null) {
			sofar.add(next);
			return convertList(getPropValue(node, RDF.rest.asNode(), context), context, sofar);
		} else {
			return sofar;
		}
	}

	/**
	 * Construct a new integer valued node
	 */
	public static Node makeIntNode(int value) {
		return NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(value));
	}

	/**
	 * Construct a new long valued node
	 */
	public static Node makeLongNode(long value) {
		if (value > Integer.MAX_VALUE) {
			return NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(value));
		} else {
			return NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral((int) value));
		}
	}

	/**
	 * Construct a new double valued node
	 */
	public static Node makeDoubleNode(double value) {
		return NodeFactory.createLiteral(LiteralLabelFactory.createTypedLiteral(value));
	}

	/**
	 * Construct an RDF list from the given array of nodes and assert it in the
	 * graph returning the head of the list.
	 */
	public static Node makeList(Node[] nodes, Graph graph) {
		return doMakeList(nodes, 0, graph);
	}

	/**
	 * Internals of makeList.
	 */
	private static Node doMakeList(Node[] nodes, int next, Graph graph) {
		if (next < nodes.length) {
			Node listNode = NodeFactory.createBlankNode();
			graph.add(new Triple(listNode, RDF.Nodes.first, nodes[next]));
			graph.add(new Triple(listNode, RDF.Nodes.rest, doMakeList(nodes, next + 1, graph)));
			return listNode;
		} else {
			return RDF.Nodes.nil;
		}
	}

	/**
	 * Open a resource file and read it all into a single string. Treats lines
	 * starting with # as comment lines, as per stringFromReader
	 */
	public static Rule.Parser loadRuleParserFromResourceFile(String filename) {
		return Rule.rulesParserFromReader(FileUtils.openResourceFile(filename));
	}

	/**
	 * Open a file defined by a URL and read all of it into a single string. If the
	 * URL fails it will try a plain file name as well.
	 */
	public static String loadURLFile(String urlStr) throws IOException {
		try (BufferedReader dataReader = FileUtils.readerFromURL(urlStr); StringWriter sw = new StringWriter(1024);) {
			char buff[] = new char[1024];
			while (dataReader.ready()) {
				int l = dataReader.read(buff);
				if (l <= 0)
					break;
				sw.write(buff, 0, l);
			}
			return sw.toString();
		}
	}

	/**
	 * Helper method - extracts the truth of a boolean configuration predicate.
	 * 
	 * @param predicate     the predicate to be tested
	 * @param configuration the configuration node
	 * @return null if there is no setting otherwise a Boolean giving the setting
	 *         value
	 */
	public static Boolean checkBinaryPredicate(Property predicate, Resource configuration) {
		StmtIterator i = configuration.listProperties(predicate);
		if (i.hasNext()) {
			return i.nextStatement().getObject().toString().equalsIgnoreCase("true");
		} else {
			return null;
		}
	}

	/**
	 * Helper method - extracts the value of an integer configuration predicate.
	 * 
	 * @param predicate     the predicate to be tested
	 * @param configuration the configuration node
	 * @return null if there is no such configuration parameter otherwise the value
	 *         as an integer
	 */
	public static Integer getIntegerPredicate(Property predicate, Resource configuration) {
		StmtIterator i = configuration.listProperties(predicate);
		if (i.hasNext()) {
			RDFObject lit = i.nextStatement().getObject();
			if (lit instanceof Literal) {
				return ((Literal) lit).getInt();
			}
		}
		return null;
	}

	/**
	 * Convert the value of a boolean configuration parameter to a boolean value.
	 * Allows the value to be specified using a String or Boolean.
	 * 
	 * @param parameter the configuration property being set (to help with error
	 *                  messages)
	 * @param value     the parameter value
	 * @return the converted value
	 * @throws IllegalParameterException if the value can't be converted
	 */
	public static boolean convertBooleanPredicateArg(Resource parameter, Object value) {
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		} else if (value instanceof String) {
			return ((String) value).equalsIgnoreCase("true");
		} else {
			throw new IllegalParameterException("Illegal type for " + parameter + " setting - use a Boolean");
		}

	}

	/**
	 * Convert the value of an integer configuration parameter to an int value.
	 * Allows the value to be specified using a String or Number.
	 * 
	 * @param parameter the configuration property being set (to help with error
	 *                  messages)
	 * @param value     the parameter value
	 * @return the converted value
	 * @throws IllegalParameterException if the value can't be converted
	 */
	public static int convertIntegerPredicateArg(Property parameter, Object value) {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException e) {
				throw new IllegalParameterException("Illegal type for " + parameter + " setting - use an integer");
			}
		} else {
			throw new IllegalParameterException("Illegal type for " + parameter + " setting - use an integer");
		}
	}

	/**
	 * Replace the value for a given parameter on the resource by a new value.
	 * 
	 * @param config    the resource whose values are to be updated
	 * @param parameter a predicate defining the parameter to be set
	 * @param value     the new value
	 */
	public static void updateParameter(Resource config, Property parameter, Object value) {
		for (StmtIterator i = config.listProperties(parameter); i.hasNext();) {
			i.next();
			i.remove();
		}
		config.addProperty(parameter, value.toString());
	}

	private static InputStream getInputStream(String uri, N3GraphConfig config) throws IOException {		
		if (uri.startsWith(filePrefix)) {
			try {
				return new FileInputStream(uri.substring(filePrefix.length()));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		} else {
			URL url = new URL(uri);

			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(config.getBuiltinConfig().getDownloadTimeout());

			return conn.getInputStream();
		}
	}

	public static String getFileUri(String path) {
		return "file://" + path.replace("\\", "/");
	}

	public static N3Model retrieveN3(String uri, N3Model out) throws IOException {
		String url = toUrl(uri);
		InputStream in = getInputStream(url, (N3GraphConfig) out.getGraph().getConfig());

		try {
			out.read(in, url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!out.hasBase())
			out.setBase(url);

		return out;
	}

	private static String toUrl(String uri) {
		try {
			// drop fragment part (shouldnt be part of document uri,
			// or when retrieving as file)

			URI uri2 = new URI(uri);
			// files tend not to have an authority; but we need a non-null one if we want a
			// "//" at the end of the scheme
			return new URI(uri2.getScheme(), (uri2.getAuthority() == null ? "" : uri2.getAuthority()), uri2.getPath(),
					null).toString();

		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Node_Collection retrieveFiles(String uri) {
		String url = toUrl(uri);

		if (url.startsWith(filePrefix)) {
			File file = new File(url.substring(filePrefix.length()));

			if (file.isDirectory()) {
				List<File> files = listFiles(file);

				Node_Collection coll = NodeFactory.createCollection(files.stream()
						.map(f -> NodeFactory.createURI(filePrefix + f.getAbsolutePath().replace("\\", "/")))
						.collect(Collectors.toList()));

				return coll;
			}
		}

		return null;
	}

	private static List<File> listFiles(File folder) {
		List<File> files = new ArrayList<>();
		listFiles(folder, files);

		return files;
	}

	private static void listFiles(File folder, List<File> files) {
		for (File file : folder.listFiles()) {
			if (file.getName().equals(".") || file.getName().equals(".."))
				continue;

			if (file.isDirectory())
				listFiles(file, files);
			else
				files.add(file);
		}
	}

	@SuppressWarnings("resource")
	public static String retrieveString(String uri, N3GraphConfig config) throws IOException {
		InputStream in = getInputStream(uri, config);

		Scanner sc = new Scanner(in, "UTF-8").useDelimiter("\\A");
		String str = (sc.hasNext() ? sc.next() : "");
		sc.close();

		return str;
	}

	public static N3Model parseN3(String content, String base) {
		N3Model out = ModelFactory.createN3Model(N3ModelSpec.get(N3_MEM));

		ParserN3 parser = new ParserN3();
		parser.parse(out, base, new StringReader(content));

		if (!out.hasBase() && base != null)
			out.setBase(base);

		return out;
	}

	public static void baseOn(N3Model newModel, N3Model orModel) {
		if (!newModel.hasBase() && orModel.hasBase())
			newModel.setBase(orModel.getBase());

		newModel.setNsPrefixes(orModel.getNsPrefixMap());
	}
}
