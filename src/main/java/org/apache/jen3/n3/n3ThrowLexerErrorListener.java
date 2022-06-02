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

package org.apache.jen3.n3;

import java.util.BitSet;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import w3c.n3dev.parser.n3LexerErrorListener;

public class n3ThrowLexerErrorListener extends n3LexerErrorListener {

	public n3ThrowLexerErrorListener(String name) {
		super(name);
	}

	@Override
	public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5,
			ATNConfigSet arg6) {

		onError();
		throw new N3ParseException(
				name + "[lexer]: " + "reportAmbiguity @" + arg2 + "-" + arg3 + " " + arg4 + " " + arg5 + " " + arg6);
	}

	@Override
	public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
		onError();
		throw new N3ParseException(
				name + "[lexer]: " + "reportAttemptingFullContext @" + arg2 + "-" + arg3 + " " + arg4 + " " + arg5);
	}

	@Override
	public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
		onError();
		throw new N3ParseException(
				name + "[lexer]: " + "reportContextSensitivity @" + arg2 + "-" + arg3 + " " + arg4 + " " + arg5);
	}

	@Override
	public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int arg2, int arg3, String arg4,
			RecognitionException arg5) {

		onError();
		throw new N3ParseException(
				name + " [lexer]: " + "syntaxError for " + arg1 + " @" + arg2 + ":" + arg3 + " - " + arg4 + " " + arg5);
	}
}