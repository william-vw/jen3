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

import org.antlr.v4.runtime.tree.TerminalNode;

import w3c.n3dev.parser.n3ParserErrorListener;
import w3c.n3dev.parser.n3PrefixException;
import w3c.n3dev.parser.antlr.n3Parser;
import w3c.n3dev.parser.n3PrefixException.PrefixErrors;

public class n3PrefixErrorVisitor extends n3PrefixVisitor {

	private n3ParserErrorListener listener;

	public n3PrefixErrorVisitor(n3ParserErrorListener listener) {
		this.listener = listener;
	}

	@Override
	public Void visitPrefixedName(n3Parser.PrefixedNameContext ctx) {
		TerminalNode pNameLn = ctx.PNAME_LN();

		if (pNameLn != null) {
			String pName = pNameLn.getText().trim();
			String prefix = pName.substring(0, pName.indexOf(":")).trim();

			if (prefix.isEmpty())
				return null;

			if (!prefixUris.containsKey(prefix))
				listener.prefixError(prefix, pName, new n3PrefixException(PrefixErrors.UNKNOWN_PREFIX));
		}

		return null;
	}

}
