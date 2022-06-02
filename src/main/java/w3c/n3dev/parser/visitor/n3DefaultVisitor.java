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

import org.antlr.v4.runtime.RuleContext;

import w3c.n3dev.parser.antlr.n3BaseVisitor;

public class n3DefaultVisitor extends n3BaseVisitor<Void> {

	protected String collectText(RuleContext ctx, String separator) {
		StringBuffer str = new StringBuffer();

		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (str.length() > 0)
				str.append(separator);
			str.append(ctx.getChild(i));
		}

		return str.toString();
	}
}
