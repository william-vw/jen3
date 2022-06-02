package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jen3.datatypes.TypeMapper;
import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinalities;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.Cardinality;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.DatatypeIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.InputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.ListIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.OneOfIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.BoolStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.OneOfStatementIC;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.StatementInputConstraint;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic.stmt.SubjectObjectStatementIC;
import org.apache.jena.atlas.logging.Log;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import com.github.javaparser.ast.type.Type;

public class BuiltinDefinitionCompiler extends BuiltinDefinitionMarshaller {

	private String genPck = null;
	private File genFolder = null;

	private CompilationUnit allBuiltinsUnit;
	private BlockStmt getAllBuiltins;

	@Override
	// only call method in case of a custom definitions file
	public boolean marshallRequired(File definSrc, BuiltinConfig config) throws Exception {
		setGenPck(definSrc, true, config);

		File allBuiltinsFile = new File(genFolder, "AllBuiltinDefinitions.java");

		return (!allBuiltinsFile.exists() || definSrc.lastModified() > allBuiltinsFile.lastModified());
//		return true;
	}

	private void setGenPck(File definSrc, boolean custom, BuiltinConfig config) {
		genPck = (custom ? config.getBuiltinDefPck() : BuiltinConfig.defaultBuiltinPck) + getId(definSrc);
		genFolder = new File("src/main/java/" + genPck.replaceAll("\\.", "/"));
	}

	@Override
	// only call method in case of a custom definitions file
	public void marshall(List<BuiltinDefinition> builtins, File definSrc, BuiltinConfig config) throws Exception {
		long start = System.currentTimeMillis();

		setGenPck(definSrc, true, config);

		compileAllBuiltinsCls();

		Map<String, String> namedInputCnstrs = new HashMap<>();

		for (BuiltinDefinition builtin : builtins) {
			InputConstraintsDefinition inputCnstr = builtin.getInputConstraints();

			String builtinClsName = getBuiltinClsName(builtin);
			String inputClsName = getInputClsName(inputCnstr, builtinClsName);

			if (!inputCnstr.hasUri() || !namedInputCnstrs.containsKey(inputCnstr.getUri())) {
				compileInputConstraints(inputCnstr, inputClsName);

				if (inputCnstr.hasUri())
					namedInputCnstrs.put(inputCnstr.getUri(), inputClsName);

			} else
				inputClsName = namedInputCnstrs.get(inputCnstr.getUri());

			compileBuiltinDefinition(builtin, builtinClsName, inputClsName);

			compileAddBuiltin(builtinClsName);
		}

		finalizeAllBuiltinsCls();

		long end = System.currentTimeMillis();
		long time = (end - start);

		Log.warn(getClass(), "finished writing Java classes (" + time + "ms) to " + genFolder);
		Log.warn(getClass(), "please re-compile before running!");
		System.exit(0);
	}

	private String getBuiltinClsName(BuiltinDefinition builtin) {
		String uri = builtin.getUri();

		String type = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("#"));
		String name = uri.substring(uri.lastIndexOf("#") + 1);
		String clsName = StringUtils.capitalize(type) + StringUtils.capitalize(name);

		return clsName;
	}

	private String getInputClsName(InputConstraintsDefinition inputCnstr, String builtinClsName) {
		String inputClsName = "Input"
				+ (inputCnstr.hasUri() ? StringUtils.capitalize(TermUtil.getLocalName(inputCnstr.getUri()))
						: builtinClsName);

		return inputClsName;
	}

	private void compileAllBuiltinsCls() {
		allBuiltinsUnit = new CompilationUnit();
		allBuiltinsUnit.setPackageDeclaration(genPck);
		allBuiltinsUnit.addImport(List.class);
		allBuiltinsUnit.addImport(ArrayList.class);
		allBuiltinsUnit.addImport(BuiltinDefinition.class);

		ClassOrInterfaceDeclaration allBuiltinsCls = allBuiltinsUnit.addClass("AllBuiltinDefinitions").setPublic(true);

		Type builtinListType = new ClassOrInterfaceType(null, new SimpleName("List"),
				new NodeList<>(new ClassOrInterfaceType(null, "BuiltinDefinition")));

		MethodDeclaration getter = new MethodDeclaration(
				new NodeList<>(Modifier.publicModifier(), Modifier.staticModifier()), builtinListType, "getAll");
		allBuiltinsCls.addMember(getter);

		getAllBuiltins = new BlockStmt();
		getter.setBody(getAllBuiltins);

		Expression newList = new ObjectCreationExpr(null,
				new ClassOrInterfaceType(null, new SimpleName("ArrayList"), new NodeList<>()), new NodeList<>());

		Expression allBuiltinsVar = new VariableDeclarationExpr(
				new VariableDeclarator(builtinListType, new SimpleName("allBuiltins"), newList));

		getAllBuiltins.addStatement(allBuiltinsVar);
	}

	private void compileAddBuiltin(String defClsName) {
		Expression newBuiltinDef = new ObjectCreationExpr(null, new ClassOrInterfaceType(null, defClsName),
				new NodeList<>());
		MethodCallExpr addStmt = new MethodCallExpr(new NameExpr("allBuiltins"), "add", new NodeList<>(newBuiltinDef));

		getAllBuiltins.addStatement(addStmt);
	}

	private void finalizeAllBuiltinsCls() throws Exception {
		getAllBuiltins.addStatement(new ReturnStmt(new NameExpr("allBuiltins")));

		storeCompilationUnit(allBuiltinsUnit, "AllBuiltinDefinitions");
	}

	private void compileBuiltinDefinition(BuiltinDefinition builtin, String builtinClsName, String inputClsName)
			throws Exception {
		CompilationUnit unit = new CompilationUnit();
		unit.setPackageDeclaration(genPck);

		if (builtin.hasImpl())
			unit.addImport(builtin.getImpl().getClass().getName());

		ClassOrInterfaceDeclaration clsDecl = unit.addClass(builtinClsName).setPublic(true);
		clsDecl.addExtendedType(BuiltinDefinition.class);

		clsDecl.addFieldWithInitializer(new PrimitiveType(Primitive.LONG), "serialVersionUID",
				new LongLiteralExpr(String.valueOf(1L)), Keyword.PRIVATE, Keyword.STATIC, Keyword.FINAL);

		ConstructorDeclaration constrDecl = clsDecl.addConstructor(Keyword.PUBLIC);
		BlockStmt blockStmt = new BlockStmt();
		constrDecl.setBody(blockStmt);

		NodeList<Expression> args = new NodeList<>();
		args.add(new StringLiteralExpr(builtin.getUri()));
		if (builtin.hasImpl())
			args.add(new ObjectCreationExpr(null,
					new ClassOrInterfaceType(null, builtin.getImpl().getClass().getSimpleName()), new NodeList<>()));
		else
			args.add(new NullLiteralExpr());
		args.add(new BooleanLiteralExpr(builtin.isResourceIntensive()));
		args.add(new BooleanLiteralExpr(builtin.isInstantiate()));
		args.add(new BooleanLiteralExpr(builtin.isStatic()));

		Statement superCall = new ExplicitConstructorInvocationStmt(false, null, args);
		blockStmt.addStatement(superCall);

		Expression newInputCnstrs = new ObjectCreationExpr(null, new ClassOrInterfaceType(null, inputClsName),
				new NodeList<>(new ThisExpr()));
		Expression setInputCnstrs = new MethodCallExpr(null, new SimpleName("setInputConstraints"),
				new NodeList<>(newInputCnstrs));

		blockStmt.addStatement(setInputCnstrs);

		storeCompilationUnit(unit, builtinClsName);
	}

	private void compileInputConstraints(InputConstraintsDefinition inputCnstr, String inputClsName) throws Exception {
		CompilationUnit unit = new CompilationUnit();
		unit.setPackageDeclaration(genPck);

		unit.addImport(BuiltinDefinition.class);

		ClassOrInterfaceDeclaration clsDecl = unit.addClass(inputClsName).setPublic(true);
		clsDecl.addExtendedType(InputConstraintsDefinition.class);

		clsDecl.addFieldWithInitializer(new PrimitiveType(Primitive.LONG), "serialVersionUID",
				new LongLiteralExpr(String.valueOf(1L)), Keyword.PRIVATE, Keyword.STATIC, Keyword.FINAL);

		ConstructorDeclaration constrDecl = clsDecl.addConstructor(Keyword.PUBLIC).addParameter("BuiltinDefinition",
				"builtin");
		BlockStmt blockStmt = new BlockStmt();
		constrDecl.setBody(blockStmt);

		NodeList<Expression> args = new NodeList<>();
		args.add(new NameExpr("builtin"));
		args.add(inputCnstr.hasUri() ? new StringLiteralExpr(inputCnstr.getUri()) : new NullLiteralExpr());

		Statement superCall = new ExplicitConstructorInvocationStmt(false, null, args);
		blockStmt.addStatement(superCall);

		unit.addImport(BuiltinClauses.class.getName(), true, true);

		for (BuiltinClauses clause : BuiltinClauses.values()) {
			if (inputCnstr.hasClause(clause)) {
				MethodCallExpr addStmt = new MethodCallExpr("addClause", new NameExpr(clause.toString()),
						compileStatementInputConstraint(inputCnstr.getClause(clause), unit));
				blockStmt.addStatement(addStmt);
			}
		}

		storeCompilationUnit(unit, inputClsName);
	}

	private Expression compileStatementInputConstraint(StatementInputConstraint inputCnstr, CompilationUnit unit) {
		unit.addImport(inputCnstr.getClass());

		NodeList<Expression> args = new NodeList<>();
		args.add(new ThisExpr());

		switch (inputCnstr.getType()) {

		case SO:
			SubjectObjectStatementIC soIc = (SubjectObjectStatementIC) inputCnstr;
			if (soIc.hasSubject())
				args.add(compileInputConstraint(soIc.getSubject(), unit));
			else
				args.add(new NullLiteralExpr());

			if (soIc.hasObject())
				args.add(compileInputConstraint(soIc.getObject(), unit));
			else
				args.add(new NullLiteralExpr());

			if (soIc.hasSubjectObject())
				args.add(compileInputConstraint(soIc.getSubjectObject(), unit));
			else
				args.add(new NullLiteralExpr());

			break;

		case ONE_OF:
			OneOfStatementIC ooIc = (OneOfStatementIC) inputCnstr;
			ooIc.getElements().stream().map(el -> compileStatementInputConstraint(el, unit))
					.forEach(ex -> args.add(ex));
			break;

		case BOOL:
			BoolStatementIC boIc = (BoolStatementIC) inputCnstr;
			args.add(new BooleanLiteralExpr(boIc.getValue()));

			break;

		default:
			break;
		}

		return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, inputCnstr.getClass().getSimpleName()),
				args);
	}

	private Expression compileInputConstraint(InputConstraint ic, CompilationUnit unit) {
		unit.addImport(ic.getClass());

		NodeList<Expression> args = new NodeList<>();
		switch (ic.getType()) {

		case ONE_OF:
			OneOfIC ooIc = (OneOfIC) ic;
			ooIc.getElements().stream().map(el -> compileInputConstraint(el, unit)).forEach(ex -> args.add(ex));

			break;

		case LIST:
			ListIC listIc = (ListIC) ic;
			if (listIc.hasSize())
				args.add(compileCardinality(listIc.getSize(), unit));
			else
				args.add(new NullLiteralExpr());

			if (listIc.hasElementType())
				args.add(compileInputConstraint(listIc.getElementType(), unit));
			else
				args.add(new NullLiteralExpr());

			if (listIc.hasElements())
				listIc.getElements().stream().map(el -> compileInputConstraint(el, unit)).forEach(ex -> args.add(ex));

			break;

		case DATATYPE:
			unit.addImport(TypeMapper.class);

			DatatypeIC dtIc = (DatatypeIC) ic;
			String dtUri = dtIc.getDatatype().getURI();

			MethodCallExpr getInst = new MethodCallExpr(new TypeExpr(new ClassOrInterfaceType(null, "TypeMapper")),
					"getInstance");
			MethodCallExpr getType = new MethodCallExpr(getInst, "getSafeTypeByName",
					new NodeList<Expression>(new StringLiteralExpr(dtUri)));

			args.add(getType);
			args.add(new BooleanLiteralExpr(dtIc.isTryCasting()));

			break;

		default:
			break;
		}

		return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, ic.getClass().getSimpleName()), args);
	}

	private Expression compileCardinality(Cardinality card, CompilationUnit unit) {
		unit.addImport(card.getClass());
		unit.addImport(card.getType().getClass().getName(), true, true);
		unit.addImport(Cardinalities.class.getName(), true, true);

		NodeList<Expression> args = new NodeList<>();
		args.add(new NameExpr(card.getType().toString()));
		args.add(new IntegerLiteralExpr(String.valueOf(card.getValue())));

		return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, card.getClass().getSimpleName()), args);
	}

	private void storeCompilationUnit(CompilationUnit unit, String clsName) throws IOException {
		String unitStr = unit.toString();

		if (!genFolder.exists())
			genFolder.mkdirs();

		File clsFile = new File(genFolder, clsName + ".java");
		IOUtils.write(unitStr, new FileOutputStream(clsFile), Charset.defaultCharset());

//		System.out.println("storing " + clsFile);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<BuiltinDefinition> unmarshall(File definSrc, boolean custom, BuiltinConfig config) throws Exception {
		setGenPck(definSrc, custom, config);

		System.out.println("(loading builtins: " + genPck + ")");
		Class cls = Class.forName(genPck + ".AllBuiltinDefinitions");
		return (List<BuiltinDefinition>) cls.getDeclaredMethod("getAll").invoke(null);
	}
}