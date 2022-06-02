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

package w3c.n3dev.parser;

import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_PARSER_ERROR;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_UNKOWN_PREFIX;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.jen3.n3.N3Feedback;
import org.apache.jen3.n3.N3ModelSpec;

public class n3ParserErrorListener extends BaseErrorListener {

	private int errorCnt = 0;
	private N3ModelSpec spec;

	public n3ParserErrorListener(N3ModelSpec spec) {
		this.spec = spec;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {

		onError();

		spec.getFeedback(SYNTAX_PARSER_ERROR).doDefaultAction(line, charPositionInLine, msg);
	}

	public void prefixError(String offendingPrefix, String context, n3PrefixException e) {
		onError();

		N3Feedback feedback = spec.getFeedback(SYNTAX_UNKOWN_PREFIX);
		if (offendingPrefix.trim().isEmpty())
			feedback.doActionAt(0);
		else
			feedback.doActionAt(1, e.getMessage(), offendingPrefix, context);
	}

	public void namespaceError(String iri, N3Feedback feedback) {
		onError();
		
		feedback.doDefaultAction(iri);
	}

	protected void onError() {
		errorCnt++;
	}

	public int getNumErrors() {
		return errorCnt;
	}
}