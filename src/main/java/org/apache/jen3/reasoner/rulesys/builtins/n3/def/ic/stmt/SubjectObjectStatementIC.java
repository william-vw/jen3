package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt;

import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.InputConstraintsDefinition;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinality;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinality.CardinalityCheck;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.InputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICCheck;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICConvert;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result.ICStatementConvert;

public class SubjectObjectStatementIC extends StatementInputConstraint {

	private static final long serialVersionUID = 735795252980821203L;

	protected static int matchId = 0;

	private static ICCheck checkSuccess = new ICCheck(true);

	private InputConstraint subject;
	private InputConstraint object;
	private InputConstraint subjectObject;

	public SubjectObjectStatementIC(InputConstraintsDefinition inputCnstr) {
		super(inputCnstr, StatementICTypes.SO);
	}

	public SubjectObjectStatementIC(InputConstraintsDefinition inputCnstr, InputConstraint subject,
			InputConstraint object) {
		super(inputCnstr, StatementICTypes.SO);

		this.subject = subject;
		this.object = object;
	}

	public SubjectObjectStatementIC(InputConstraintsDefinition inputCnstr, InputConstraint subjectObject) {
		super(inputCnstr, StatementICTypes.SO);

		this.subjectObject = subjectObject;
	}

	public SubjectObjectStatementIC(InputConstraintsDefinition inputCnstr, InputConstraint subject,
			InputConstraint object, InputConstraint subjectObject) {
		
		super(inputCnstr, StatementICTypes.SO);
		
		this.subject = subject;
		this.object = object;
		this.subjectObject = subjectObject;
	}

	public InputConstraint getSubject() {
		return subject;
	}

	public boolean hasSubject() {
		return subject != null;
	}

	public void setSubject(InputConstraint subject) {
		this.subject = subject;
	}

	public InputConstraint getObject() {
		return object;
	}

	public boolean hasObject() {
		return object != null;
	}

	public void setObject(InputConstraint object) {
		this.object = object;
	}
	
	public boolean hasSubjectObject() {
		return subjectObject != null;
	}

	public InputConstraint getSubjectObject() {
		return subjectObject;
	}

	public void setSubjectObject(InputConstraint subjectObject) {
		this.subjectObject = subjectObject;
	}

	public ICCheck check(Node s, Node o, Graph graph) {
		int id = matchId++;

		for (StmtElements target : StmtElements.values()) {
			InputConstraint tr = (subjectObject == null ? target.get(subject, object) : subjectObject);
			if (tr == null)
				continue;

			Node n = target.get(s, o);

			ICTrace trace = createTrace(target, graph);
			if (!tr.check(n, id, graph, trace))
				return new ICCheck(false, trace);

			// for separate s/o clauses, check separately
			if (subjectObject == null) {
				CardinalityCheck check = Cardinality.checkClause();
				if (!check.isSuccess())
					return new ICCheck(false, check.getTrace());
			}
		}

		// for combined s/o clause, check together
		if (subjectObject != null) {
			CardinalityCheck check = Cardinality.checkClause();
			if (!check.isSuccess())
				return new ICCheck(false, check.getTrace());
		}

		return checkSuccess;
	}

	public ICStatementConvert convert(Node s, Node o, Graph graph) {
		int id = matchId++;

		ICStatementConvert success = new ICStatementConvert(true, this);
		for (StmtElements target : StmtElements.values()) {
			InputConstraint tr = (subjectObject == null ? target.get(subject, object) : subjectObject);
			Node n = target.get(s, o);

			ICTrace trace = createTrace(target, graph);
			ICConvert ret = tr.convert(n, id, graph, trace);

			if (!ret.isSuccess())
				return new ICStatementConvert(false, trace, this);
			else {
				// for separate s/o clauses, check separately
				if (subjectObject == null) {
					CardinalityCheck check = Cardinality.checkClause();
					if (!check.isSuccess())
						return new ICStatementConvert(false, check.getTrace(), this);
				}

				success.set(target, ret);
			}
		}

		// for combined s/o clause, check together
		if (subjectObject != null) {
			CardinalityCheck check = Cardinality.checkClause();
			if (!check.isSuccess())
				return new ICStatementConvert(false, check.getTrace(), this);
		}

		return success;
	}

	public String toString() {
		if (subjectObject == null)
			return "{\n  subject = " + subject + "\n  object = " + object + "\n}";
		else
			return "{\n  subjectObject = " + subjectObject + "\n}";
	}
}
