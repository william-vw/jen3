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

import java.io.Reader;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import w3c.n3dev.parser.n3LexerErrorListener;
import w3c.n3dev.parser.n3LogLexerErrorListener;
import w3c.n3dev.parser.n3ParserErrorListener;
import w3c.n3dev.parser.antlr.n3Lexer;
import w3c.n3dev.parser.antlr.n3Parser;

/**
 * Parser for N3.
 * 
 * 
 * @author wvw
 *
 */

public class ParserN3 {

	public ParserN3() {
	}

	public void parse(N3Model model, String baseURI, Reader reader) {
		try {
			n3LexerErrorListener lexerErrors = new n3LogLexerErrorListener(baseURI, model.getSpec());

			n3Lexer lexer = new n3Lexer(CharStreams.fromReader(reader));
			lexer.removeErrorListeners();
			lexer.addErrorListener(lexerErrors);

			n3ParserErrorListener parserErrors = new n3ParserErrorListener(model.getSpec());

			n3Parser parser = new n3Parser(new CommonTokenStream(lexer));
			parser.removeErrorListeners();
			parser.addErrorListener(parserErrors);
			parser.removeParseListeners();
			parser.addParseListener(new N3GraphInserter(baseURI, model, parserErrors));

			parser.n3Doc();

		} catch (Exception e) {
			e.printStackTrace();
			throw new N3ParseException(e.getMessage(), e);
		}
	}

}