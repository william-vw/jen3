package org.apache.jen3.n3;

import static org.apache.jen3.n3.N3MistakeRelevancy.*;

public enum N3MistakeTypes {

	// @formatter:off
	
	// catch-all ; easy to add custom listener for all mistakes
	ALL(N3MistakeRelevancy.ALL, (String[]) null),
	
	SYNTAX_PARSER_ERROR(SYNTAX,
			"[N3Parser]: parserError at line %s (char %d) - %s)"),
	
	SYNTAX_LEXER_ERROR(SYNTAX,
			"[N3Lexer]: reportAmbiguity @%s-%s %s %s %s",
			"[N3Lexer]: reportAttemptingFullContext @%s-%s %s %s",
			"[N3Lexer]: reportContextSensitivity @%s-%s %s %s",
			"[N3Lexer]: lexerError @%s:%s - %s %s"),
	
	SYNTAX_UNKOWN_PREFIX(SYNTAX, 
			// (lack of an empty-prefix will no longer flag as an error; the empty prefix is initialized with the document URI)
			"[N3Parser]: prefixError: missing empty prefix namespace. \nWhen you use an iri with an empty prefix (such as :hello) "
					+ "you need to specify an empty prefix namespace (i.e., @prefix : <your-namespace>)", 
			"[N3Parser]: prefixError: %s (\"%s\") in \"%s\""), 
	
	SYNTAX_PRED_RESOURCE_PATH(SYNTAX, 
			"Using resource paths in the predicate position is often a mistake. See https://w3c.github.io/N3/spec/#paths for details."),

	SYNTAX_MALFORMED_RULE(SYNTAX, "This rule has an unexpected head or body:\n%s => %s\nSee https://w3c.github.io/N3/spec/#logimpl for details."),
	
	BUILTIN_WRONG_INPUT(BUILTINS, 
			"Wrong input for %s:\n%s\n"),
	
	BUILTIN_UNBOUND_VARS(BUILTINS, 
			"Some variables weren't bound for %s:\n%s\n"),
	
	BUILTIN_MISUSE_NS(BUILTINS, 
			"[N3Parser]: namespaceError: using a builtin namespace but IRI is not a known builtin: <%s>.\n"
					+ "If you're making custom builtins, it's best to introduce your own namespace "
					+ "since future N3 versions may introduce a builtin with this IRI."),
	
	BUILTIN_STATIC_DATA(BUILTINS,
			"Static N3 builtin %s is not allowed in data. See https://w3c.github.io/N3/spec/#builtins for details."),
	
	BUILTIN_NO_BASE(BUILTINS,
			"One of the builtin assertions will never succeed, since the builtin requires a base IRI but none is provided. "
			+ "See https://w3c.github.io/N3/spec/#builtins for details."),
	
	INFER_UNSUPP_BACKWARD(INFER, 
			"Rules with backward reasoning (<=) are not supported by this reasoner - "
			+ "forward chaining will be used for the following rule:\n"
			+ "%s <= %s"),
	
	INFER_UNSUPP_FORWARD(INFER, 
			"Rules with forward reasoning (=>) are not supported by this reasoner - "
			+ "forward chaining will be used for the following rule:\n"
			+ "%s <= %s"),
	
	INFER_UNSUPP_BECOMES(INFER, 
			"Linear logic implications (log:becomes) are currently not supported:\n%s"),
	
	INFER_MISUSE_STABLE_TRUTH(INFER, 
			"Misusing term log:StableTruth - it is meant to type a quoted graph that represents the stable truth in a linear logic implication "
			+ "(see jen3 documentation for details):"
			+ "\n%s"),
	
	INFER_UNBOUND_GLOBALS(INFER, 
			"unbound universal variable (\"%s\") is being inferred for rule:\n%s"),
	
	INFER_INFERENCE_FUSE(INFER, 
			"blew an inference fuse (false inferred) for rule:\n%s\nSee https://w3c.github.io/N3/spec/#logimpl for details.");
	// @formatter:on

	private N3MistakeRelevancy relevancy;
	private String[] msgs;

	private N3MistakeTypes(N3MistakeRelevancy relevancy, String... msgs) {
		this.relevancy = relevancy;
		this.msgs = msgs;
	}

	public String getMessage() {
		return msgs[0];
	}

	public String getMessage(int nr) {
		return msgs[nr];
	}

	public N3MistakeRelevancy getRelevancy() {
		return relevancy;
	}

	public boolean traceInputCheck() {
		switch (this) {

		case BUILTIN_WRONG_INPUT:
		case BUILTIN_UNBOUND_VARS:
			return true;

		default:
			return false;
		}
	}

	public boolean isCatchAll() {
		return this == ALL;
	}
}
