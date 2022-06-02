package org.apache.jen3.n3.impl.skolem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.reasoner.rulesys.impl.BindingStack;

public class UniqueIdViaKey extends UniqueNodeGen {

	private static int nextFormulaId = 0;
	private static Map<Node, Integer> formulaMap = new HashMap<>();

	private static MessageDigest digester;

	static {
		try {
			digester = MessageDigest.getInstance("MD5");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public Node uniqueBlankNode(BindingStack env, Node node, Scope scope) {
		Stream<Node> nodes = Stream.concat(Stream.of(node), Stream.of(env.getEnvironment()));
		String id = uniqueId(nodes);

		return NodeFactory.createBlankNode(id, scope);
	}

	// create a unique id per blank node
//	public Node uniqueSkolem(Node bnode, N3ModelSpec spec) {
//		String id = uniqueId(Stream.of(bnode));
//	
//		return NodeFactory.createURI(N3Skolem.uri + id);
//	}

	public static String uniqueId(List<Node> bindings) {
		return uniqueId(bindings.stream());
	}
	
	public static String uniqueId(Node ... bindings) {
		return uniqueId(Arrays.stream(bindings));
	}

	// based on org.apache.jena.reasoner.rulesys.builtins.MakeSkolem
	private static String uniqueId(Stream<Node> bindings) {
		StringBuilder key = new StringBuilder();

		bindings.forEach(e -> {
			if (e == null) {
				key.append("N");
				return;
			}

			switch (e.getType()) {

			case BLANK:
				key.append("B");
				key.append(e.getBlankNodeLabel());
				break;

			case URI:
				key.append("U");
				key.append(e.getURI());
				break;

			case LITERAL:
				key.append("L");
				key.append(e.getLiteralLexicalForm());
				if (e.getLiteralLanguage() != null)
					key.append("@" + e.getLiteralLanguage());
				if (e.getLiteralDatatypeURI() != null)
					key.append("^^" + e.getLiteralDatatypeURI());
				break;

			// cannot use the string form since ordering in cited formulas doesn't matter
			// (and we haven't figured out a canonical form)

			// identical hash-code should be returned for semantically equivalent formulas:
			// but a hash collision could occur, i.e., different formulas w/ same hash-code

			// so we need to keep a map with encountered cited formulas
			// (for hash collisions, map should do equals() check as well, so good there)

			case CITED_FORMULA:
				key.append("G");
				key.append(uniqueFormulaId(e));
				break;

			default:
				key.append("O");
				key.append(e.toString());
				break;
			}
		});

		// we don't really care about the length of the environment
		// (when called from BindingStack)

		// we're happy as long as they have the same contents
		// (binding envs can be extended when data with variables is pulled in)

		String keyStr = key.toString();

		// remove nulls at the end of the binding environment
		// (don't care about length of the environment)
		keyStr = keyStr.replaceAll("^(.*?)N+$", "$1");

//		System.out.println("key? " + keyStr);

		digester.reset();

		byte[] digest = digester.digest(keyStr.getBytes());
		String label = Base64.encodeBase64URLSafeString(digest);

		return label;
	}

	private static int uniqueFormulaId(Node cf) {
//		System.out.println(formulaMap);

		if (formulaMap.containsKey(cf))
			return formulaMap.get(cf);
		else {
			int id = nextFormulaId++;
			formulaMap.put(cf, id);
			return id;
		}
	}
}