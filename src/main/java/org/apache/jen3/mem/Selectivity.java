package org.apache.jen3.mem;

import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.reasoner.TriplePattern;

public class Selectivity {

	// some ad-hoc tests indicate that S/O/P is slightly faster than O/S/P
	// (of course this will always depend on the concrete use case

	private static final byte[] order = { Triple.S, Triple.O, Triple.P };
//	private static final byte[] order = { Triple.O, Triple.S, Triple.P };

	public static byte chooseIndex(Triple t, boolean matchAbsolute) {
		byte ret = Byte.MAX_VALUE;
		for (byte spo : order) {
			Node n = t.get(spo);
			if (n.isConcrete()) {
				// if concrete & hashable, return
				if (n.isHashable())
					return spo;

				// assign first concrete spo
				else if (ret == Byte.MAX_VALUE)
					ret = spo;
			}
		}

		// if no hashable's are found, return first concrete spo
		return ret;
	}

	public static byte chooseIndex(TriplePattern t, boolean matchAbsolute) {
		byte ret = Byte.MAX_VALUE;
		for (byte spo : order) {
			Node n = t.get(spo);
			if (n.isConcrete()) {
				// if concrete & hashable, return
				if (n.isHashable())
					return spo;

				// assign first concrete spo
				else if (ret == Byte.MAX_VALUE)
					ret = spo;
			}
		}

		// if no hashable's are found, return first concrete spo
		return ret;
	}
}
