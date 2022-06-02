package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.result;

import java.util.List;

import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ICTrace;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint.StmtElements;

public class ICStatementConvert extends ICResult {

	private StatementInputConstraint cnstr;

	private ICConvert subject;
	private ICConvert object;

	public ICStatementConvert(boolean success, StatementInputConstraint cnstr) {
		super(success);
		
		this.cnstr = cnstr;
	}

	public ICStatementConvert(boolean success, ICConvert subject, ICConvert object, StatementInputConstraint cnstr) {
		super(success);

		this.subject = subject;
		this.object = object;
		
		this.cnstr = cnstr;
	}

	public ICStatementConvert(boolean success, ICTrace trace, StatementInputConstraint cnstr) {
		super(success, trace);
		
		this.cnstr = cnstr;
	}

	public ICStatementConvert(boolean success, List<ICTrace> traces, StatementInputConstraint cnstr) {
		super(success, traces);
		
		this.cnstr = cnstr;
	}

	public void set(StmtElements target, ICConvert ret) {
		switch (target) {

		case SUBJECT:
			subject = ret;
			break;
		case OBJECT:
			object = ret;
			break;
		}
	}
	
	public StatementInputConstraint getConstraint() {
		return cnstr;
	}

	public ICConvert getSubject() {
		return subject;
	}

	public ICConvert getObject() {
		return object;
	}

	public String toString() {
		return subject + ", " + object;
	}
}
