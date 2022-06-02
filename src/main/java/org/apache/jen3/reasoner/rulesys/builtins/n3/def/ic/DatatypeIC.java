package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.TypeMapper;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;
import org.apache.jena.atlas.logging.Log;

public class DatatypeIC extends InputConstraint {

	private static final long serialVersionUID = 1464451822911795329L;

	protected boolean tryCasting = true;
	protected RDFDatatype datatype;

	public DatatypeIC() {
		super(DefaultICs.DATATYPE);
	}

	public DatatypeIC(RDFDatatype datatype, boolean tryCasting) {
		super(DefaultICs.DATATYPE);

		this.datatype = datatype;
		this.tryCasting = tryCasting;
	}

	public DatatypeIC(DefaultICs type, RDFDatatype datatype, boolean tryCasting) {
		super(type);

		this.datatype = datatype;
		this.tryCasting = tryCasting;
	}

	public void setDatatype(Resource dt) {
		setDatatype(dt.getURI());
	}

	public RDFDatatype getDatatype() {
		return datatype;
	}

	public boolean isTryCasting() {
		return tryCasting;
	}

	private void setDatatype(String dtUri) {
		this.datatype = TypeMapper.getInstance().getSafeTypeByName(dtUri);
		if (datatype == null)
			Log.error(getClass(), "cannot find datatype: " + dtUri);
	}

	@Override
	protected boolean doCheck(Node n, int id, Graph graph, ICTrace trace) {
		boolean ret = Util.parseLiteral(n, datatype) != null;

		return ret;
	}

	@Override
	public ICConvert doConvert(Node n, int id, Graph graph, ICTrace trace) {
		if (n.isLiteral()) {
			if (n.getLiteralDatatype().equals(datatype))
				return new ICConvert(n);

			else if (tryCasting) {
				Object value = Util.parseLiteral(n, datatype);
				if (value != null)
					return new ICConvert(NodeFactory.createLiteralByValue(value, datatype));
			}
		}

		return noMatch;
	}

	private void writeObject(ObjectOutputStream o) throws IOException {
		// InputConstraint superclass
		o.writeObject(cardinality);
		o.writeObject(type);

		o.writeBoolean(tryCasting);
		o.writeInt(datatype.getURI().length());
		o.writeBytes(datatype.getURI());
	}

	private void readObject(ObjectInputStream i) throws IOException, ClassNotFoundException {
		// InputConstraint superclass
		cardinality = (Cardinality) i.readObject();
		type = (DefaultICs) i.readObject();

		tryCasting = i.readBoolean();

//		String dtUri = new String(i.readNBytes(i.readInt()));

		byte[] bytes = new byte[i.readInt()];
		i.read(bytes, 0, bytes.length);
		String dtUri = new String(bytes);

		setDatatype(dtUri);
	}

	@Override
	public String toString() {
		return "<DATATYPE=" + datatype + (cardinality != null ? " (" + cardinality + ")" : "")
				+ ">";
	}
}
