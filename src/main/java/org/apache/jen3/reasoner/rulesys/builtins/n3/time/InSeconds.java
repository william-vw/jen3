package org.apache.jen3.reasoner.rulesys.builtins.n3.time;

import java.util.Calendar;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.datatypes.xsd.XSDDateTime;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class InSeconds extends TimeBuiltin {

	public InSeconds() {
		super(new BinaryFlowPattern((n,g) -> {
			XSDDateTime t = (XSDDateTime) n.getLiteralValue();
			long millis = t.asCalendar().getTimeInMillis();
			int sec = Math.round(millis / 1000);

			return NodeFactory.createLiteralByValue(sec, XSDDatatype.XSDint);

		}, (n,g) -> {
			int sec = Util.parseInt(n);
			long millis = ((long) sec) * 1000;
			
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(millis);

			XSDDateTime t = new XSDDateTime(c);
			Node n2 = NodeFactory.createLiteralByValue(t, XSDDatatype.XSDdateTime);

			return n2;

		}), true);
	}
}
