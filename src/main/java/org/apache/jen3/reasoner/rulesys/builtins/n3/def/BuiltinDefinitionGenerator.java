package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.jen3.rdf.model.impl.TermUtil;
import org.apache.jen3.reasoner.rulesys.builtins.n3.N3Builtin;
import org.apache.jena.atlas.logging.Log;

public class BuiltinDefinitionGenerator {

	protected ResolveJavaClass resolver = new ResolveJavaClass("org.apache.jen3.reasoner.rulesys.builtins.n3");

	protected static class ResolveJavaClass {

		// assumes use of swap namespaces
		private Pattern p = Pattern.compile(".*/(.*)#.*");
		private String root;

		public ResolveJavaClass(String root) {
			this.root = root;
		}

		public N3Builtin resolve(BuiltinDefinition def) throws Exception {
			String uri = def.getUri();

			Matcher m = p.matcher(uri);
			if (!m.matches())
				throw new Exception(
						"expecting SWAP-like namespace for builtin (i.e., http://(.*)/(package)#(class)), got " + uri);

			String pck = m.group(1);

			String clsName = StringUtils.capitalize(TermUtil.getLocalName(uri));

			String qualCls = root + "." + pck + "." + clsName;
			try {
				Class<?> impl = Class.forName(qualCls);

				N3Builtin ret = (N3Builtin) impl.newInstance();
				ret.setDefinition(def);

				return ret;

			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				Log.error(BuiltinDefinitionMarshaller.class, "cannot find or instantiate class " + qualCls);
				e.printStackTrace();

				return null;
			}
		}
	}
}
