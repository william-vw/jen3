package org.apache.jen3.reasoner;

import org.apache.jen3.graph.Triple;
import org.apache.jen3.util.iterator.ExtendedIterator;
import org.apache.jen3.util.iterator.NiceIterator;

public class DummyFinder implements Finder {

	@Override
	public ExtendedIterator<Triple> find(TriplePattern pattern) {
		return NiceIterator.emptyIterator();
	}

	@Override
	public ExtendedIterator<Triple> findWithContinuation(TriplePattern pattern,
			Finder continuation) {
		return continuation.find(pattern);
	}

	@Override
	public boolean contains(TriplePattern pattern) {
		return false;
	}
}
