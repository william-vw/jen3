/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.graph.n3.scope;

import java.util.ArrayList;
import java.util.List;

public class Scope implements Comparable<Scope> {

	public static enum Scopes {
		GRAPH // QUANTIFIER
	}

	public static Scope outer() {
		return new Scope(Scopes.GRAPH, 0);
	}

	protected ScopedObject scoped;
	protected int lvl = -1;
	protected Scopes type;

	protected Scope parent;
	protected List<Scope> children = new ArrayList<>();

	public Scope(Scopes type) {
		this.type = type;
	}

	protected Scope(Scopes type, int lvl) {
		this.type = type;
		this.lvl = lvl;
	}

	public Scope sub(Scopes scoped) {
		Scope scope = create(scoped, lvl + 1);
		return scope;
	}

	public Scope leveled(Scopes scoped) {
		Scope scope = create(scoped, lvl);
		return scope;
	}

	private Scope create(Scopes type, int lvl) {
		Scope scope = new Scope(type, lvl);
		addChild(scope);

		return scope;
	}

	protected void accept(ScopeVisitor visitor) {
		visitor.visit(this);
	}

	public Scopes getType() {
		return type;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		if (this.lvl != lvl) {
			this.lvl = lvl;
			children.stream().forEach(s -> s.setLvl(lvl + 1));
		}
	}

	public boolean sameLvl(Scope scope) {
		return lvl == scope.getLvl();
	}

	public boolean hasChild(Scope child) {
		return children.contains(child);
	}

	public void addChild(Scope child) {
		children.add(child);
		child.parent = this;
	}

	public void removeChild(Scope child) {
		children.remove(child);
		child.parent = null;
	}
	
	public void clear() {
		children.clear();
	}

	public List<Scope> getChildren() {
		return children;
	}

	public void attach(ScopedObject scoped) {
		this.scoped = scoped;
		scoped.setScope(this);
	}
	
	public boolean hasScoped() {
		return scoped != null;
	}

	public ScopedObject getScoped() {
		return scoped;
	}

	public boolean isOuter() {
		return lvl == 0;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public boolean hasParent(Scopes type) {
		return parent != null && parent.getType() == type;
	}

	public Scope getParent() {
		return parent;
	}

	public void setParent(Scope parent) {
		this.parent = parent;
	}

	public String printHierarchy() {
		return new ScopePrintVisitor().print(this);
	}

	@Override
	public int compareTo(Scope s) {
		return Integer.compare(lvl, s.getLvl());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Scope))
			return false;

		Scope s = (Scope) o;
		return s.getLvl() == lvl;
	}

	@Override
	public String toString() {
		return "@" + lvl + "";
	}

	protected interface ScopeVisitor {

		public void visit(Scope scope);
	}

	protected class ScopePrintVisitor implements ScopeVisitor {

		private StringBuffer str = new StringBuffer();
		private int lvl = 0;

		public String print(Scope scope) {
			scope.accept(this);

			return str.toString();
		}

		public void visit(Scope scope) {
			append(scope);
			lvl++;
			scope.getChildren().stream().forEach(child -> child.accept(this));
			lvl--;
		}

		private void append(Scope scope) {
			str.append("\n");
			for (int i = 0; i < lvl; i++)
				str.append(" ");

			str.append(scope);
			if (scope.getScoped() != null)
				str.append(" - ").append(scope.getScoped());
		}
	}
}
