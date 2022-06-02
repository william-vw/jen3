package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICCompositeConvert;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;

public abstract class CompositeIC extends InputConstraint {

	private static final long serialVersionUID = -3276802685921580625L;

	private Cardinality size;
	private InputConstraint elementType;
	private List<InputConstraint> elements;

	private int curId;
	private ICTrace curTrace;
	private Graph curGraph;

	private ICCompositeConvert inputs;

	public CompositeIC(DefaultICs type) {
		super(type);
	}

	public CompositeIC(DefaultICs type, Cardinality size, InputConstraint elementType, List<InputConstraint> elements) {
		this.type = type;
		this.size = size;
		this.elementType = elementType;
		this.elements = elements;
	}

	public boolean hasSize() {
		return size != null;
	}

	public Cardinality getSize() {
		return size;
	}

	public void setSize(Cardinality size) {
		this.size = size;
	}

	public boolean hasElementType() {
		return elementType != null;
	}

	public InputConstraint getElementType() {
		return elementType;
	}

	public void setElementType(InputConstraint elementType) {
		this.elementType = elementType;
	}

	public boolean hasElements() {
		return elements != null && !elements.isEmpty();
	}

	public void add(InputConstraint tr) {
		if (elements == null)
			elements = new ArrayList<>();
		elements.add(tr);
	}

	public List<InputConstraint> getElements() {
		return elements;
	}

	public void setElements(List<InputConstraint> elements) {
		this.elements = elements;
	}

	@Override
	protected boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		if (!checkCompositeType(n))
			return false;

		init(id, trace, graph);
		return process(n, this::checkElFn, this::checkElTypeFn, trace);
	}

	// don't do separate check (avoid going over elements twice)
	// instead, just do all work in one go, incl. checking

	@Override
	protected ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (!checkCompositeType(n))
			return noMatch;

		init(id, trace, graph);
		return (process(n, this::convertElFn, this::convertElTypeFn, trace) ? inputs : noMatch);
	}

	// - checking

	private boolean checkElFn(InputConstraint d, Node ln) {
		return d.check(ln, curId, curGraph, curTrace);
	}

	private boolean checkElTypeFn(Node ln) {
		return elementType.check(ln, curId, curGraph, curTrace);
	}

	// - converting

	private boolean handleInput(ICConvert input) {
		if (input.isSuccess()) {
			inputs.add(input);
			return true;

		} else
			return false;
	}

	private boolean convertElFn(InputConstraint d, Node ln) {
		return handleInput(d.convert(ln, curId, curGraph, curTrace));
	}

	private boolean convertElTypeFn(Node ln) {
		return handleInput(elementType.convert(ln, curId, curGraph, curTrace));
	}

	private void init(int id, ICTrace trace, Graph graph) {
		this.curId = id;
		this.curTrace = trace;
		this.curGraph = graph;
	}

	protected abstract boolean checkCompositeType(Node n);

	protected abstract ICCompositeConvert getCompositeConvert();

	protected abstract ICCompositeConvert getCompositeConvert(Node n);

	protected abstract List<Node> getCompositeElements(Node n);

	private boolean process(Node n, BiFunction<InputConstraint, Node, Boolean> elFn, Function<Node, Boolean> elDomFn,
			ICTrace trace) {

		List<Node> list = getCompositeElements(n);

		if (size != null && !size.matches(list.size(), true))
			return false;

		Iterator<Node> listIt = list.iterator();

		if (hasElements() || hasElementType()) {
			this.inputs = getCompositeConvert();

			if (hasElements())
				return processElements(listIt, elFn, trace);

			else if (hasElementType())
				return processElementType(listIt, elDomFn, trace);

		} else
			this.inputs = getCompositeConvert(n);

		return true;
	}

	private boolean processElements(Iterator<Node> listIt, BiFunction<InputConstraint, Node, Boolean> fn,
			ICTrace trace) {

		Iterator<InputConstraint> it = elements.iterator();

		while (it.hasNext()) {
			if (!listIt.hasNext())
				return false;

			InputConstraint d = it.next();
			Node ln = listIt.next();

			if (!fn.apply(d, ln))
				return false;
		}

		if (listIt.hasNext())
			return false;

		return true;
	}

	private boolean processElementType(Iterator<Node> listIt, Function<Node, Boolean> fn, ICTrace trace) {
		while (listIt.hasNext()) {
			Node ln = listIt.next();

			boolean ret = fn.apply(ln);
			if (!ret)
				return false;
		}

		return true;
	}

	public String toString() {
		String ret = "<" + type + (cardinality != null ? " (" + cardinality + ")" : "");
		if (size != null)
			ret += " ; size = " + size;
		if (hasElementType())
			ret += " ; elementType = " + elementType;
		if (hasElements())
			ret += " ; elements = " + elements;
		ret += ">";

		return ret;
	}
}
