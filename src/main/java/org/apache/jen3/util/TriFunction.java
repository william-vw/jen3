package org.apache.jen3.util;

public interface TriFunction <A, B, C, X> {

	public X apply(A a, B b, C c);
}
