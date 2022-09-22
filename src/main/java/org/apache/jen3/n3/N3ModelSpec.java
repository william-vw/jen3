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

import static org.apache.jen3.n3.FeedbackActions.LOG;
import static org.apache.jen3.n3.FeedbackActions.NONE;
import static org.apache.jen3.n3.FeedbackActions.THROW_EXC;
import static org.apache.jen3.n3.FeedbackTypes.ERROR;
import static org.apache.jen3.n3.FeedbackTypes.WARN;
import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_MISUSE_NS;
import static org.apache.jen3.n3.N3MistakeTypes.BUILTIN_STATIC_DATA;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_INFERENCE_FUSE;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_MISUSE_STABLE_TRUTH;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_UNBOUND_GLOBALS;
import static org.apache.jen3.n3.N3MistakeTypes.INFER_UNSUPP_BACKWARD;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_LEXER_ERROR;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_MALFORMED_RULE;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_PARSER_ERROR;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_PRED_RESOURCE_PATH;
import static org.apache.jen3.n3.N3MistakeTypes.SYNTAX_UNKOWN_PREFIX;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.jen3.graph.Node;
import org.apache.jen3.n3.impl.N3ModelImpl.N3GraphConfig;
import org.apache.jen3.n3.scope.N3ScopeScheme.N3ScopeTypes;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.ModelFactory;
import org.apache.jen3.rdf.model.ModelMaker;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.Reasoner;
import org.apache.jen3.reasoner.ReasonerFactory;
import org.apache.jen3.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jen3.reasoner.rulesys.GenericRuleReasoner.RuleMode;
import org.apache.jen3.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinSet;
import org.apache.jen3.vocabulary.N3Skolem;
import org.apache.jen3.vocabulary.ReasonerVocabulary;

/**
 * Encapsulates a description of the components of an N3 model, including the
 * storage scheme, reasoner and scoping scheme.
 * 
 * (based on OntModelSpec)
 * 
 * @author wvw
 */

public class N3ModelSpec {

	public enum Types {
		N3_MEM, N3_MEM_FP_INF, N3_MEM_RETE_INF, N3_MEM_LP_INF, N3_MEM_HYBRID_INF
	}

	/**
	 * A specification for N3 models that are stored in memory and do no additional
	 * entailment reasoning
	 */
	private static final N3ModelSpec N3_MEM = new N3ModelSpec(ModelFactory.createMemModelMaker());

	/**
	 * A specification for N3 models that are stored in memory and use the generic
	 * rule reasoner, with a forward-chaining fixpoint algorithm, for entailments
	 */
	private static final N3ModelSpec N3_MEM_FP_INF = new N3ModelSpec(ModelFactory.createMemModelMaker(),
			GenericRuleReasonerFactory.theInstance(), GenericRuleReasoner.FORWARD);

	/**
	 * A specification for N3 models that are stored in memory and use the generic
	 * rule reasoner, with a forward-chaining RETE algorithm, for entailments
	 */
	private static final N3ModelSpec N3_MEM_RETE_INF = new N3ModelSpec(ModelFactory.createMemModelMaker(),
			GenericRuleReasonerFactory.theInstance(), GenericRuleReasoner.FORWARD_RETE);

	/**
	 * A specification for N3 models that are stored in memory and use the generic
	 * rule reasoner, with a LP algorithm for entailments
	 */
	private static final N3ModelSpec N3_MEM_LP_INF = new N3ModelSpec(ModelFactory.createMemModelMaker(),
			GenericRuleReasonerFactory.theInstance(), GenericRuleReasoner.BACKWARD);

	/**
	 * A specification for N3 models that are stored in memory and use the generic
	 * rule reasoner, with a hybrid forward-chaining fixpoint and LP algorithm for
	 * entailments
	 */

	// NOTE
	private static final N3ModelSpec N3_MEM_HYBRID_INF = new N3ModelSpec(ModelFactory.createMemModelMaker(),
			GenericRuleReasonerFactory.theInstance(), GenericRuleReasoner.HYBRID_N3);

	private ModelMaker maker;
	private ReasonerFactory reasonerFactory;
	private Resource config;
	private RuleMode ruleMode;
	private N3ScopeTypes scopeType = N3ScopeTypes.NONE;
	private boolean loadBuiltins;
	private BuiltinConfig builtinConfig = new BuiltinConfig(this);
	private Map<N3MistakeTypes, N3Feedback> feedbacks = new HashMap<>();
	private String skolemNs;

	private List<Node> allowRederivesFor = new ArrayList<>();

	public N3ModelSpec(ModelMaker maker) {
		this(maker, null);

		// often need to know what builtins are available (e.g., for raising errors)
		// even when not an inferencing model
		loadBuiltins = true;
	}

	public N3ModelSpec(ModelMaker maker, N3ScopeTypes scopeType) {
		this.maker = maker;
		if (scopeType != null)
			this.scopeType = scopeType;

		setupFeedbacks();
	}

	public N3ModelSpec(ModelMaker maker, ReasonerFactory reasonerFactory, RuleMode ruleMode) {
		this(maker, reasonerFactory, ruleMode, null, null, (N3Feedback[]) null);
	}

	public N3ModelSpec(ModelMaker maker, ReasonerFactory reasonerFactory, RuleMode ruleMode, N3ScopeTypes scopeType,
			String builtinDefPath, N3Feedback... feedbacks) {

		this.maker = maker;
		this.reasonerFactory = reasonerFactory;

		Model m = ModelFactory.createN3Model(N3ModelSpec.N3_MEM);
		config = m.createResource();
		config.addProperty(ReasonerVocabulary.PROPruleMode, ruleMode.toString());

		this.ruleMode = ruleMode;

		if (scopeType != null)
			this.scopeType = scopeType;

		loadBuiltins = true;
		if (builtinDefPath != null)
			builtinConfig.setBuiltinDefPath(builtinDefPath);

		setupFeedbacks();
	}

	private N3ModelSpec(ModelMaker maker, ReasonerFactory reasonerFactory, Resource config, RuleMode ruleMode,
			N3ScopeTypes scopeType, boolean loadBuiltins, BuiltinConfig builtinConfig,
			Map<N3MistakeTypes, N3Feedback> feedbacks) {

		this.maker = maker;
		this.reasonerFactory = reasonerFactory;
		this.config = config;
		this.ruleMode = ruleMode;
		this.scopeType = scopeType;
		this.feedbacks = feedbacks;
		this.loadBuiltins = loadBuiltins;
		this.builtinConfig = builtinConfig;
	}

	public static N3ModelSpec get(Types type) {
		switch (type) {

		case N3_MEM:
			return N3_MEM.copy();

		case N3_MEM_FP_INF:
			return N3_MEM_FP_INF.copy();

		case N3_MEM_RETE_INF:
			return N3_MEM_RETE_INF.copy();

		case N3_MEM_LP_INF:
			return N3_MEM_LP_INF.copy();

		case N3_MEM_HYBRID_INF:
			return N3_MEM_HYBRID_INF.copy();
		}

		return null;
	}

	private static MessageDigest digester;

	private void setupFeedbacks() {
		setFeedback(new N3Feedback(SYNTAX_PARSER_ERROR, ERROR, THROW_EXC));
		setFeedback(new N3Feedback(SYNTAX_LEXER_ERROR, ERROR, THROW_EXC));
		setFeedback(new N3Feedback(SYNTAX_UNKOWN_PREFIX, ERROR, THROW_EXC));
		setFeedback(new N3Feedback(INFER_INFERENCE_FUSE, ERROR, THROW_EXC));
		setFeedback(new N3Feedback(BUILTIN_STATIC_DATA, ERROR, LOG));
//		setFeedback(new N3Feedback(BUILTIN_NO_BASE, WARN, LOG));
		setFeedback(new N3Feedback(SYNTAX_MALFORMED_RULE, WARN, LOG));
		setFeedback(new N3Feedback(BUILTIN_MISUSE_NS, WARN, LOG));
		setFeedback(new N3Feedback(INFER_UNSUPP_BACKWARD, WARN, LOG));
//		setFeedback(new N3Feedback(INFER_UNSUPP_BECOMES, ERROR, THROW_EXC));
		setFeedback(new N3Feedback(INFER_MISUSE_STABLE_TRUTH, WARN, LOG));
		setFeedback(new N3Feedback(INFER_UNBOUND_GLOBALS, ERROR, THROW_EXC));
		setFeedback(new N3Feedback(SYNTAX_PRED_RESOURCE_PATH, WARN, LOG));
	}

	public ModelMaker getMaker() {
		return maker;
	}

	public ReasonerFactory getReasonFactory() {
		return reasonerFactory;
	}

	public N3ModelSpec setRules(String rulesPath) {
		config.addProperty(ReasonerVocabulary.PROPruleSet, rulesPath);

		return this;
	}

	/**
	 * <p>
	 * Create an N3Model according to this model specification. The base model comes
	 * from the attached base ModelMaker.
	 * </p>
	 * 
	 * @return an N3Model satisfying this specification
	 */

	public Model createBaseModel() {
		return ModelFactory.createDefaultModel();
	}

	public boolean isInf() {
		return reasonerFactory != null;
	}

	/**
	 * <p>
	 * Answer the reasoner that will be used to infer additional entailed triples in
	 * the N3 model.
	 * </p>
	 * 
	 * @return The reasoner for this specification
	 */
	public Reasoner getReasoner() {
		if (reasonerFactory != null) {
			// we need to create the reasoner
			// create a new one on each call since reasoners aren't guaranteed to be
			// reusable

			return reasonerFactory.create(config);
		}

		return null;
	}

	public RuleMode getRuleMode() {
		return ruleMode;
	}

	public N3ScopeTypes getScopeType() {
		return scopeType;
	}

	public N3ModelSpec setScopeType(N3ScopeTypes scopeType) {
		this.scopeType = scopeType;
		return this;
	}

	public boolean loadBuiltins() {
		return loadBuiltins;
	}

	public N3ModelSpec setLoadBuiltins(boolean loadBuiltins) {
		this.loadBuiltins = loadBuiltins;
		return this;
	}

	public BuiltinConfig getBuiltinConfig() {
		return builtinConfig;
	}

	public BuiltinSet getBuiltinSet() {
		return builtinConfig.getSet();
	}

	public boolean hasFeedbackFor(N3MistakeTypes... mistakes) {
		return Arrays.stream(mistakes).anyMatch(m -> {
			return feedbacks.containsKey(m) && feedbacks.get(m).getAction() != NONE;
		});
	}

	public N3Feedback getFeedback(N3MistakeTypes mistake) {
		return feedbacks.get(mistake);
	}

	public N3ModelSpec setFeedback(N3Feedback... feedbacks) {
		Arrays.stream(feedbacks).forEach(feedback -> {

			if (feedback.getMistake() == N3MistakeTypes.ALL) {
				this.feedbacks.values().stream().forEach(f -> {
					f.overrideAction(feedback);
				});

			} else
				this.feedbacks.put(feedback.getMistake(), feedback);

			if (feedback.getMistake().traceInputCheck())
				builtinConfig.traceInputCheck(true);
		});
		return this;
	}

	public Map<N3MistakeTypes, N3Feedback> getFeedbacks() {
		return feedbacks;
	}

	public boolean allowRederives() {
		return !allowRederivesFor.isEmpty();
	}

	public List<Node> getRederivePredicates() {
		return allowRederivesFor;
	}

	public void allowRederiveFor(Node... predicates) {
		for (Node predicate : predicates)
			allowRederivesFor.add(predicate);
	}

	public String getSkolemNs() {
		// only generate this when needed - e.g., could be set from parent spec
		// (see from() method)

		if (skolemNs == null)
			genSkolemNs();

		return skolemNs;
	}

	private void genSkolemNs() {
		String ran = UUID.randomUUID().toString();

		if (digester == null)
			try {
				digester = MessageDigest.getInstance("MD5");

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		digester.reset();

		byte[] digest = digester.digest(ran.getBytes());
		String label = Base64.encodeBase64URLSafeString(digest);

		skolemNs = N3Skolem.uri + label + "#";
//		skolemNs = N3Skolem.uri;
	}

	public N3ModelSpec copy() {
		// don't copy skolemNs - have to explicitly call from() method
		return new N3ModelSpec(maker, reasonerFactory, config, ruleMode, scopeType, loadBuiltins, builtinConfig.copy(),
				feedbacks);
	}

	public N3ModelSpec from(N3ModelSpec parentSpec) {
		this.skolemNs = parentSpec.getSkolemNs();
		this.feedbacks = new HashMap<>(parentSpec.getFeedbacks());
		return this;
	}

	public N3ModelSpec from(N3GraphConfig config) {
		this.builtinConfig = config.getBuiltinConfig();
		this.feedbacks = new HashMap<>(config.getFeedbacks());
		return this;
	}
}