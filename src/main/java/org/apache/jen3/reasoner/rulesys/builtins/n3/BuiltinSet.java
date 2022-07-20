package org.apache.jen3.reasoner.rulesys.builtins.n3;

import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_MISUSE_NS;
import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jen3.graph.Node;
import org.apache.jen3.n3.FeedbackActions;
import org.apache.jen3.n3.N3Feedback;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.n3.N3ModelSpec;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinitionMarshaller;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinitionMarshaller.BuiltinMarshallTypes;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinitionParser;
import org.apache.jen3.util.IOUtils;
import org.apache.jen3.vocabulary.N3Log;
import org.apache.jen3.vocabulary.N3Reason;
import org.apache.jena.atlas.logging.Log;

public class BuiltinSet {

	// cache builtin sets for re-use
	// expectation is that most models will rely on the same set of builtins
	private static Map<String, BuiltinSet> builtinSets = new HashMap<>();

	private Map<String, N3Builtin> builtins = new HashMap<>();
	private Set<String> reservedTerms = new HashSet<>();
	private Set<String> reservedNs = new HashSet<>();

	// due to internal workings of Jena, builtin-set are often saved as a field
	// (e.g., FRuleEngine) but before the set is loaded, resulting in a nullpointer

	// rather than doing "lazy loading" in all those instances, it seems easier to
	// always return the same builtin-set and then load its state later on

	public static void load(BuiltinConfig config, BuiltinSet newSet) {
		String builtinDefPath = config.getBuiltinDefPath();
		BuiltinSet loadedSet = builtinSets.get(builtinDefPath);
		if (loadedSet == null) {
			Log.debug(N3Builtin.class, "loading builtins definitions from new path");

			newSet.loadBuiltins(config);
			builtinSets.put(builtinDefPath, newSet);

		} else {
			Log.debug(N3Builtin.class, "re-using builtin definitions loaded previously");

			// after loading, builtin-sets are immutable
			// so we're safe to re-use internal state of prior loaded set
			newSet.from(loadedSet);
		}
	}

	private void loadBuiltins(BuiltinConfig config) {
		long start = System.currentTimeMillis();

		boolean custom = false;
		File definSrc = null;
		InputStream definIn = null;
		if (config.isDefaultBuiltinDefPath())
			try {
				definSrc = new File(BuiltinConfig.defaultBuiltinDefPath);
				definIn = IOUtils.getResourceInputStream(BuiltinSet.class, "/" + config.getBuiltinDefPath());

			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			}

		else {
			custom = true;
			definSrc = new File(config.getBuiltinDefPath());
			try {
				definIn = new FileInputStream(definSrc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		List<BuiltinDefinition> definitions = null;
		try {
			if (config.getMarshallBuiltins() == BuiltinMarshallTypes.PARSE_EACH_TIME)
				definitions = parseBuiltinDefinitions(definIn);
			else {
				BuiltinDefinitionMarshaller marshaller = BuiltinDefinitionMarshaller
						.create(config.getMarshallBuiltins());

				// assuming only custom definitions file will be edited
				if (custom && marshaller.marshallRequired(definSrc, config)) {
					Log.warn(N3Builtin.class, "sorry - gotta parse builtin definitions!");
					definitions = parseBuiltinDefinitions(definIn);

					marshaller.marshall(definitions, definSrc, config);
				}

				definitions = marshaller.unmarshall(definSrc, custom, config);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		definitions.stream().forEach((d) -> register(d));

		reservedNs.add(N3Log.uri);
		reservedNs.add(N3Reason.uri);

		long end = System.currentTimeMillis();
		long time = (end - start);
		Log.warn(BuiltinSet.class, "loaded definitions (" + time + "ms)");
	}

	private List<BuiltinDefinition> parseBuiltinDefinitions(InputStream definIn) throws Exception {
		N3ModelSpec builtinSpec = N3ModelSpec.get(N3_MEM);

		// here we'll be "misusing" the SWAP namespace for sure ..
		// so let's disable this feedback
		builtinSpec.setFeedback(new N3Feedback(BUILTIN_MISUSE_NS, FeedbackActions.NONE));

		// also, let's not get into a loop ..
		builtinSpec.setLoadBuiltins(false);

		N3Model m = ModelFactory.createN3Model(builtinSpec);
		m.read(definIn, null);

		BuiltinDefinitionParser parser = new BuiltinDefinitionParser();
		List<BuiltinDefinition> definitions = parser.parse(m);

		return definitions;
	}

	public boolean isBuiltin(Node predicate) {
		if (predicate.isURI())
			return builtins.containsKey(predicate.getURI());
		else
			return false;
	}

	public boolean isStatic(Node predicate) {
		if (predicate.isURI())
			return builtins.containsKey(predicate.getURI()) && builtins.get(predicate.getURI()).isStatic();
		else
			return false;
	}

	public boolean isBuiltinTerm(Node term) {
		if (term.isURI())
			return builtins.containsKey(term.getURI()) || reservedTerms.contains(term.getURI())
					|| reservedNs.contains(term.getNameSpace());
		else
			return false;
	}

	public N3Builtin getBuiltin(Node predicate) {
		if (predicate.isURI())
			return builtins.get(predicate.getURI());
		else
			return null;
	}

	private void register(BuiltinDefinition def) {
		String uri = def.getUri();
		if (def.hasImpl())
			builtins.put(uri, def.getImpl());
		else
			builtins.put(uri, new DummyN3Builtin(def));
	}

	public void from(BuiltinSet loadedSet) {
		builtins = loadedSet.builtins;
		reservedTerms = loadedSet.reservedTerms;
		reservedNs = loadedSet.reservedNs;
	}

	private static class DummyN3Builtin extends N3Builtin {

		public DummyN3Builtin(BuiltinDefinition def) {
			super(def);
		}
	}
}
