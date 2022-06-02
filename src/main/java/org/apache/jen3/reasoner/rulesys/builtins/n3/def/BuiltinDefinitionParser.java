package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jen3.rdf.model.Collection;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.rdf.model.Statement;
import org.apache.jen3.rdf.model.StmtIterator;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinalities;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinality;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.DatatypeIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.InputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.InputConstraint.DefaultICs;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.OneOfStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jen3.vocabulary.BuiltinNS;
import org.apache.jen3.vocabulary.RDF;

public class BuiltinDefinitionParser extends BuiltinDefinitionGenerator {

//	public static void main(String[] args) throws Exception {
//		N3Model m = ModelFactory.createN3Model(N3ModelSpec.N3_MEM);
//		m.read(new FileInputStream(new File("src/main/resources/etc/builtins/builtins_liberal.n3")), null);
//
//		Map<String, BuiltinDefinition> builtins = new BuiltinDefinitionParser().parse(m);
//		System.out.println(builtins.values().stream().map(v -> v.toString()).collect(Collectors.joining("\n\n")));
//	}

	// settings

	// default return value when some variables aren't bound for builtins
	// (notBound clause)
	public static boolean defaultNotBound = false;

	private Map<Resource, DefaultICs> defaultIcTypes = new HashMap<>();
	private Map<Resource, Cardinalities> cardinalityTypes = new HashMap<>();

	public BuiltinDefinitionParser() {
		initEnums();
	}

	private void initEnums() {
		defaultIcTypes = new HashMap<>();
		for (DefaultICs type : DefaultICs.values())
			defaultIcTypes.put(type.getUri(), type);

		cardinalityTypes = new HashMap<>();
		for (Cardinalities c : Cardinalities.values())
			cardinalityTypes.put(c.getUri(), c);
	}

	public List<BuiltinDefinition> parse(Model m) throws BuiltinParserException {
		List<BuiltinDefinition> builtins = new ArrayList<>();

		Iterator<Statement> it = m.listStatements(null, RDF.type, BuiltinNS.BuiltinDef);
		while (it.hasNext()) {
			Resource uri = it.next().getSubject();

			BuiltinDefinition builtin = parseBuiltin(uri);

			if (builtin.isInstantiate())
				try {
					builtin.setImpl(resolver.resolve(builtin));
				} catch (Exception e) {
					throw new BuiltinParserException(e);
				}

//			if (builtin.hasImpl())
			builtins.add(builtin);
		}

		return builtins;
	}

	private BuiltinDefinition parseBuiltin(Resource uri) throws BuiltinParserException {
		BuiltinDefinition builtin = new BuiltinDefinition(uri);

		StmtIterator it = uri.listProperties(BuiltinNS.inputConstraints);
		while (it.hasNext()) {
			Statement stmt = it.next();

			if (it.hasNext())
				throw new BuiltinParserException(
						"a builtin can only have one set of input constraints (" + builtin.getUri() + ")");

			Resource input = stmt.getObject();
			InputConstraintsDefinition inputDef = new InputConstraintsDefinition(input, builtin);
			builtin.setInputConstraints(inputDef);

			for (BuiltinClauses clause : BuiltinClauses.values()) {
				boolean read = readClause(clause, input, clause.getPrp(), inputDef);
				if (!read) {

					switch (clause) {
					
					case NOT_BOUND:
						inputDef.addClause(clause, new BoolStatementIC(inputDef, defaultNotBound));
						break;

					default:
						throw new BuiltinParserException(
								"expecting " + clause + " clause for builtin " + builtin.getUri());
					}
				}
			}
		}

		builtin.setResourceIntensive(uri.hasLiteral(BuiltinNS.isResourceIntensive, true));
//		builtin.setUniversal(uri.hasLiteral(BuiltinNS.isUniversal, true));
		builtin.setInstantiate(!uri.hasLiteral(BuiltinNS.instantiate, false));
		builtin.setStatic(!uri.hasLiteral(BuiltinNS.isStatic, false));
//		builtin.setLowPriority(uri.hasLiteral(BuiltinNS.isLowPriority, true));

		return builtin;
	}

	private boolean readClause(BuiltinClauses clause, Resource uri, Resource prp, InputConstraintsDefinition inputCnstr)
			throws BuiltinParserException {

		StmtIterator it = uri.listProperties(prp);
		if (!it.hasNext())
			return false;

		while (it.hasNext()) {
			Statement stmt = it.next();

			if (inputCnstr.hasClause(clause))
				throw new BuiltinParserException("a builtin can only have one clause of type " + clause + " ("
						+ inputCnstr.getBuiltin().getUri() + ")");

			Resource value = stmt.getObject();

			StatementInputConstraint ic = readStatementIC(value, clause, inputCnstr);
			inputCnstr.addClause(clause, ic);
		}
		return true;
	}

	private StatementInputConstraint readStatementIC(Resource res, BuiltinClauses clause,
			InputConstraintsDefinition inputCnstr) throws BuiltinParserException {

		if (res.isLiteral())
			return new BoolStatementIC(inputCnstr, res.asLiteral().getBoolean());

		if (res.hasProperty(BuiltinNS.oneOf)) {
			OneOfStatementIC ic = new OneOfStatementIC(inputCnstr);
			res.getPropertyResourceValue(BuiltinNS.oneOf).asCollection().getElements().forEachRemaining(el -> {
				try {
					ic.add(readStatementIC(el, clause, inputCnstr));

				} catch (BuiltinParserException e) {
					e.printStackTrace();
				}
			});

			return ic;

		} else if (res.hasProperty(BuiltinNS.subjectObject)) {
			SubjectObjectStatementIC ic = new SubjectObjectStatementIC(inputCnstr);
			ic.setSubjectObject(readICOf(res, BuiltinNS.subjectObject, ic));

			return ic;

		} else {
			SubjectObjectStatementIC ic = new SubjectObjectStatementIC(inputCnstr);
			ic.setSubject(readICOf(res, BuiltinNS.subject, ic));
			ic.setObject(readICOf(res, BuiltinNS.object, ic));

			if (!ic.hasSubject())
				throw new BuiltinParserException(
						"no subject constraint found for " + clause + " (" + inputCnstr.getUri() + ")");

			if (!ic.hasObject())
				throw new BuiltinParserException(
						"no object constraint found for " + clause + " (" + inputCnstr.getUri() + ")");

			return ic;
		}
	}

	private InputConstraint readICOf(Resource uri, Resource prp, StatementInputConstraint scope)
			throws BuiltinParserException {

		Statement stmt = uri.getProperty(prp);
		if (stmt == null)
			return null;

		Resource o = stmt.getObject();
		return readIC(o, scope);
	}

	private InputConstraint readIC(Resource uri, StatementInputConstraint scope) throws BuiltinParserException {
		BuiltinDefinition builtin = scope.getBuiltin();

		DefaultICs icType = null;
		if (uri.hasProperty(BuiltinNS.oneOf))
			icType = DefaultICs.ONE_OF;

		else if (uri.hasProperty(BuiltinNS.listElements) || uri.hasProperty(BuiltinNS.listElementType))
			icType = DefaultICs.LIST;

		else if (uri.hasProperty(BuiltinNS.restricts)) {
			Resource restr = uri.getPropertyResourceValue(BuiltinNS.restricts);
			return readIC(restr, scope);

		} else if (uri.hasProperty(BuiltinNS.datatype))
			icType = DefaultICs.DATATYPE;

		else if (uri.hasProperty(RDF.type)) {
			Resource type = uri.getPropertyResourceValue(RDF.type);
			if (defaultIcTypes.containsKey(type))
				icType = defaultIcTypes.get(type);

		} else {
			throw new BuiltinParserException("unknown input constraint: " + uri + " (" + builtin.getUri()
					+ ")\nfound statements: " + uri.listProperties().toList());
		}

		if (icType == null)
			throw new BuiltinParserException("unknown input constraint: " + uri + " (" + builtin.getUri()
					+ ")\nfound statements: " + uri.listProperties().toList());

		InputConstraint ic = InputConstraint.create(icType);

		if (uri.hasProperty(BuiltinNS.cardinality)) {
			Cardinality c = readCardinality(uri.getPropertyResourceValue(BuiltinNS.cardinality), scope);
			ic.setCardinality(c);
		}

		Iterator<Resource> elIt;
		switch (icType) {

		case ONE_OF:
			OneOfIC union = new OneOfIC();

			Collection types = uri.getPropertyResourceValue(BuiltinNS.oneOf).asCollection();
			elIt = types.getElements();
			while (elIt.hasNext())
				union.add(readIC(elIt.next(), scope));

			return union;

		case LIST:
			ListIC listTr = (ListIC) ic;

			if (uri.hasProperty(BuiltinNS.size)) {
				Cardinality c = readCardinality(uri.getPropertyResourceValue(BuiltinNS.size), scope);
				listTr.setSize(c);
			}

			if (uri.hasProperty(BuiltinNS.listElementType)) {
				InputConstraint etTr = readIC(uri.getPropertyResourceValue(BuiltinNS.listElementType), scope);
				listTr.setElementType(etTr);
			}

			if (uri.hasProperty(BuiltinNS.listElements)) {
				Collection elements = uri.getPropertyResourceValue(BuiltinNS.listElements).asCollection();
				elements.getElements().forEachRemaining(el -> {
					try {
						listTr.add(readIC(el, scope));

					} catch (BuiltinParserException e) {
						e.printStackTrace();
					}
				});
			}

			break;

		case DATATYPE:
			Resource dt = uri.getPropertyResourceValue(BuiltinNS.datatype);
			((DatatypeIC) ic).setDatatype(dt);

			break;

		default:
			break;
		}

		return ic;
	}

	private Cardinality readCardinality(Resource uri, StatementInputConstraint scope) throws BuiltinParserException {
		BuiltinDefinition builtin = scope.getBuiltin();

		if (!uri.hasProperty(RDF.type))
			throw new BuiltinParserException("expecting type for cardinality (" + builtin.getUri() + ")");

		Resource c = uri.getPropertyResourceValue(RDF.type);
		if (!cardinalityTypes.containsKey(c))
			throw new BuiltinParserException("unknown type for cardinality: " + c + "(" + builtin.getUri() + ")");

		Cardinalities ct = cardinalityTypes.get(c);

		if (!uri.hasProperty(RDF.value))
			throw new BuiltinParserException("expecting value for cardinality (" + builtin.getUri() + ")");

		Resource v = uri.getPropertyResourceValue(RDF.value);
		if (!Util.isLongType(v.asNode()))
			throw new BuiltinParserException("expecting number for cardinality value (" + builtin.getUri() + ")");

		int vl = Util.getIntValue(v.asNode());

		Cardinality ret = new Cardinality(ct, vl);
		return ret;
	}

	private class BuiltinParserException extends Exception {

		private static final long serialVersionUID = 1L;

		public BuiltinParserException(Exception e) {
			super(e);
		}

		public BuiltinParserException(String msg) {
			super(msg);
		}
	}
}
