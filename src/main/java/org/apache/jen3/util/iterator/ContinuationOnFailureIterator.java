package org.apache.jen3.util.iterator;

import java.util.Iterator;
import java.util.function.Function;

import org.apache.jen3.graph.Triple;

public class ContinuationOnFailureIterator extends TripleForMatchIterator {

	private Function<Void, Iterator<? extends Triple>> continuation;

	private boolean foundMatch = false;
	private boolean triedCtu = false;

	public ContinuationOnFailureIterator(Iterator<? extends Triple> base,
			Function<Void, Iterator<? extends Triple>> continuation) {

		super(base);

		this.continuation = continuation;
	}

	@Override
	public void foundMatch(boolean found) {
		if (found)
			this.foundMatch = true;
	}

	@Override
	public boolean hasNext() {
		if (base.hasNext())
			return true;

		else {
			// don't try continuation if a match was found
			if (foundMatch || triedCtu)
				return false;

			base = continuation.apply(null);
			triedCtu = true;

			return hasNext();
		}
	}
}
