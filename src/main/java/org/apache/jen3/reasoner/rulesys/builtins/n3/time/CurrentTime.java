package org.apache.jen3.reasoner.rulesys.builtins.n3.time;

import java.util.Calendar;

import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.datatypes.xsd.XSDDateTime;
import org.apache.jen3.graph.NodeFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class CurrentTime extends TimeBuiltin {

	public CurrentTime() {
		super(new BinaryFlowPattern((n,g) -> {			
			Calendar now = Calendar.getInstance();
			XSDDateTime t = new XSDDateTime(now);

			return NodeFactory.createLiteralByValue(t, XSDDatatype.XSDdateTime);

		}, (n,g) -> {
			XSDDateTime t = (XSDDateTime) n.getLiteralValue();
			Calendar now = Calendar.getInstance();
			boolean eq = t.asCalendar().equals(now);

			return NodeFactory.createLiteralByValue(eq, XSDDatatype.XSDboolean);

		}), true);
	}
}
