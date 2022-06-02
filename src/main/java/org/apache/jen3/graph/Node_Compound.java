package org.apache.jen3.graph;

import java.util.List;

import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;

public abstract class Node_Compound extends Node_Concrete {

	// used by compound terms (cited-formula, collection)
	protected Node_Compound cached;

	protected Node_Compound(Object label) 
		{ super(label); }
	
	public boolean hasCached() 
		{ return cached != null; }

	public void resetCached() 
		{ cached = null; }
	
	public void setCached(Node_Compound cached)
		{ this.cached = cached; }
	
	public Node_Compound getCached() 
		{ return cached; }
	
	// to be overwritten by subclasses
	public abstract List<Node_RuleVariable> getRuleVars();
}
