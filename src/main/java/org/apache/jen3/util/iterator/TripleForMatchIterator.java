package org.apache.jen3.util.iterator;

import java.util.Iterator;

import org.apache.jen3.graph.Triple;

public class TripleForMatchIterator extends WrappedIterator<Triple> {

	public TripleForMatchIterator(Iterator<? extends Triple> base) {
		super(base);
	}

	// default: do nothing
	public void foundMatch(boolean found) {
	}
}
