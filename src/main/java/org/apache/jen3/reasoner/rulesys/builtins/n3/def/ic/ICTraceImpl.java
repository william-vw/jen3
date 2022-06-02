package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.util.ArrayList;
import java.util.List;

import org.apache.jen3.graph.Node;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint.StmtElements;

public class ICTraceImpl implements ICTrace{

	private StmtElements target;

	private TraceEntry root;
	private TraceEntry cur;

	public ICTraceImpl(StmtElements target) {
		this.target = target;
	}

	public void push(InputConstraint tr, Node node) {
		TraceEntry e = new TraceEntry(tr, node);
		if (root == null)
			root = e;
		else
			cur.addChild(e);
		cur = e;

//		Log.info(getClass(), "push = " + cur);
	}

	public int mark() {
		return cur.getDepth();
	}

	public void pop() {
//		Log.info(getClass(), "pop = " + cur);

		cur = cur.pop();
	}

	public void rewind(int mark) {
//		Log.info(getClass(), "rewind? " + mark);

		int steps = (cur.getDepth() - mark);
		while (steps-- > 0)
			cur = cur.getParent();
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		traceToString(root, 0, str);

		return (target != null ? "- " + target : "") + "\n" + str.toString().trim();
	}

	private void traceToString(TraceEntry entry, int tab, StringBuffer str) {
		if (str.length() > 0) {
			str.append("\n");
			for (int i = 0; i < tab; i++)
				str.append(" ");
			str.append(" <-- ");
		}
		str.append(entry);

		entry.getChildren().stream().forEach(child -> traceToString(child, tab + 4, str));
	}

	public class TraceEntry {

		private InputConstraint tr;
		private Node node;

		private int depth = 0;
		private TraceEntry parent;
		private List<TraceEntry> children = new ArrayList<>();

		private TraceEntry(InputConstraint tr, Node node) {
			this.tr = tr;
			this.node = node;
		}

		public InputConstraint getTypeRestriction() {
			return tr;
		}

		public Node getNode() {
			return node;
		}

		public TraceEntry getParent() {
			return parent;
		}

		public TraceEntry pop() {
			if (parent != null) {
				parent.children.remove(this);
				return parent;

			} else
				return this;
		}

		public List<TraceEntry> getChildren() {
			return children;
		}

		public void addChild(TraceEntry e) {
			children.add(e);

			e.parent = this;
			e.depth = depth + 1;
		}

		public int getDepth() {
			return depth;
		}

		@Override
		public String toString() {
			return tr + (children.isEmpty() ? " (input: [" + node.getType() + "] " + node + ")" : "");
		}
	}
	
	public static class ICTraceImplFactory implements ICTraceFactory {
		
		public ICTrace create(StmtElements target) {
			return new ICTraceImpl(target);
		}
	}
}
