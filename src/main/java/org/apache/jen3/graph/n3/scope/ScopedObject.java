package org.apache.jen3.graph.n3.scope;

public interface ScopedObject {

	public boolean hasScope();
	
	public Scope getScope();
	
	public void setScope(Scope scope);
}
