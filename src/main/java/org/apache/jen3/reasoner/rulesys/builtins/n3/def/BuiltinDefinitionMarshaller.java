package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.io.File;
import java.util.List;

import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;

public abstract class BuiltinDefinitionMarshaller extends BuiltinDefinitionGenerator {

	protected String getId(File definSrc) {
		return definSrc.getName().substring(0, definSrc.getName().indexOf("."));
	}

	public abstract boolean marshallRequired(File definSrc, BuiltinConfig config) throws Exception;

	public abstract void marshall(List<BuiltinDefinition> builtins, File definSrc, BuiltinConfig config)
			throws Exception;

	public abstract List<BuiltinDefinition> unmarshall(
			File definSrc, boolean custom, BuiltinConfig config)
			throws Exception;

	public static BuiltinDefinitionMarshaller create(BuiltinMarshallTypes type) {
		switch (type) {

		case SERIALIZE:
			return new BuiltinDefinitionSerializer();

		case COMPILE:
			return new BuiltinDefinitionCompiler();

		default:
			return null;
		}
	}

	public static enum BuiltinMarshallTypes {

		SERIALIZE, COMPILE, PARSE_EACH_TIME;
	}
}
