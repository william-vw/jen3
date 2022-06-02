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

import static org.apache.jen3.n3.N3ModelSpec.Types.N3_MEM;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jen3.n3.N3ModelSpec;

public class n3Grammar {

	private Lexer lexer;
	private Parser parser;
	private n3LexerErrorListener lexerListener;
	private n3ParserErrorListener parserListener;

	public ParserRuleContext parse(File file, String grammar) throws Exception {
		createGrammarComponents(grammar, file);

		Method m = findParseMethod(parser);
		if (m != null)
			return (ParserRuleContext) m.invoke(parser);

		else {
			System.err.println("error: could not find parser method for " + grammar);
			return null;
		}
	}

	public n3LexerErrorListener getLexerListener() {
		return lexerListener;
	}

	public n3ParserErrorListener getParserListener() {
		return parserListener;
	}

	public int getNumErrors() {
		return lexerListener.getNumErrors() + parserListener.getNumErrors();
	}

	private Method findParseMethod(Parser parser) {
		Pattern p = Pattern.compile(".*Doc");
		for (Method m : parser.getClass().getMethods()) {

			if (p.matcher(m.getName()).matches())
				return m;
		}

		return null;
	}

	private void createGrammarComponents(String grammar, File file) throws Exception {
		Pair<Class<? extends Lexer>, Class<? extends Parser>> clss = getGrammarClasses(grammar);

		lexer = clss.getLeft().getConstructor(CharStream.class)
				.newInstance(CharStreams.fromPath(file.toPath(), Charset.forName("UTF-8")));

		// (antlr 4.4)
		// new ANTLRInputStream(new InputStreamReader(new FileInputStream(file),
		// Charset.forName("UTF-8"))));

//		lexer.removeErrorListeners();
		lexerListener = new n3LogLexerErrorListener(file.getName(), N3ModelSpec.get(N3_MEM));
		lexer.addErrorListener(lexerListener);

		parser = clss.getRight().getConstructor(TokenStream.class).newInstance(new CommonTokenStream(lexer));

//		parser.removeErrorListeners();
		parserListener = new n3ParserErrorListener(N3ModelSpec.get(N3_MEM));
		parser.addErrorListener(parserListener);
	}

	@SuppressWarnings("unchecked")
	private Pair<Class<? extends Lexer>, Class<? extends Parser>> getGrammarClasses(String grammar)
			throws ClassNotFoundException {

		return ImmutablePair.of((Class<? extends Lexer>) Class.forName("parser.antlr." + grammar + "Lexer"),
				(Class<? extends Parser>) Class.forName("parser.antlr." + grammar + "Parser"));
	}
}
