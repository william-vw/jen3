package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;
import org.apache.jen3.vocabulary.BuiltinNS;

public abstract class InputConstraint extends ICBase {

	private static final long serialVersionUID = 3631339612604934285L;

	public enum DefaultICs {

		CONCRETE(BuiltinNS.ConcreteIC, ConcreteIC.class), VARIABLE(BuiltinNS.VariableIC, VariableIC.class),
		IRI(BuiltinNS.IriIC, IriIC.class), NUMBERABLE(BuiltinNS.NumberableIC, NumberableIC.class),
		INTABLE(BuiltinNS.IntableIC, IntableIC.class), STRINGABLE(BuiltinNS.StringableIC, StringableIC.class),
		STRING(BuiltinNS.StringIC, StringIC.class), REGEX(BuiltinNS.RegexIC, RegexIC.class),
		DATATYPE(BuiltinNS.DatatypeIC, DatatypeIC.class), LITERAL(BuiltinNS.LiteralIC, LiteralIC.class),
		BASE_IRI(BuiltinNS.BaseIriIC, BaseIriIC.class), FORMULA(BuiltinNS.FormulaIC, FormulaIC.class),
		ANY(BuiltinNS.AnyIC, AnyIC.class), LIST(BuiltinNS.ListIC, ListIC.class),
		ONE_OF(BuiltinNS.UnionIC, OneOfIC.class);

		private Resource uri;
		private Class<?> cls;

		private DefaultICs(Resource uri, Class<?> cls) {
			this.uri = uri;
			this.cls = cls;
		}

		public Resource getUri() {
			return uri;
		}

		public Class<?> getCls() {
			return cls;
		}
	}

	protected ICConvert noMatch = new ICConvert(false, this);

	protected Cardinality cardinality;
	protected DefaultICs type;

	public InputConstraint() {
	}

	public InputConstraint(DefaultICs type) {
		this.type = type;
	}

	public InputConstraint(Cardinality cardinality, DefaultICs type) {
		this.cardinality = cardinality;
		this.type = type;
	}

	public static InputConstraint create(DefaultICs type) {
		try {
			return (InputConstraint) type.getCls().newInstance();

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public DefaultICs getType() {
		return type;
	}

	public Cardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

	// to be overwritten by subclasses who utilize datatype
	// (see also DefaultICs#supportsDt)
	public void setDatatype(Resource dt) {
	}

	public boolean check(Node n, int id, Graph graph, ICTrace trace) {
		trace.push(this, n);

		if (doCheck(n, id, graph, trace) && checkCardinality(n, id, trace)) {
			trace.pop();
			return true;
		}

		return false;
	}

	protected abstract boolean doCheck(Node n, int id, Graph graph, ICTrace trace);

	// don't just separately call check(..) here;
	// for many builtins, this requires duplicate work when converting
	// so, leave it up to subclasses to check

	public ICConvert convert(Node n, int id, Graph graph, ICTrace trace) {
		trace.push(this, n);

		ICConvert ret = doConvert(n, id, graph, trace);
		if (ret.isSuccess() && checkCardinality(n, id, trace)) {
			trace.pop();
			return ret;
		}

		return noMatch;
	}

	protected abstract ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace);

	protected boolean checkCardinality(Node n, int id, ICTrace trace) {
		return (cardinality != null ? cardinality.check(n, id, trace) : true);
	}

	@Override
	public String toString() {
		return "<" + type + (cardinality != null ? " (" + cardinality + ")" : "") + ">";
	}
}