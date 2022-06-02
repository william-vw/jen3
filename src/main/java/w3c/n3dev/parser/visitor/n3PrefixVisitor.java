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

package w3c.n3dev.parser.visitor;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.TerminalNode;

import w3c.n3dev.parser.antlr.n3Parser;

public class n3PrefixVisitor extends n3DefaultVisitor {

	protected Map<String, String> prefixUris = new HashMap<>();

	@Override
	public Void visitPrefixID(n3Parser.PrefixIDContext ctx) {
//		String decl = collectText(ctx, " ");

		TerminalNode pNameNs = ctx.PNAME_NS();
		if (pNameNs == null)
			return null;

		String prefix = pNameNs.getText().trim();
		prefix = prefix.substring(0, prefix.length() - 1);

//		if (prefixUris.containsKey(prefix))
//			listener.prefixError(prefix, decl, new n3PrefixException(PrefixErrors.REDEF_PREFIX));

		String uri = ctx.IRIREF().getText().trim();
		prefixUris.put(prefix, uri);

		return null;
	}

	public Map<String, String> getPrefixUris() {
		return prefixUris;
	}
}