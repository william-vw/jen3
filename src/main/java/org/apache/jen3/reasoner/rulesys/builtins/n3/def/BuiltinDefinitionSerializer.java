package org.apache.jen3.reasoner.rulesys.builtins.n3.def;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.jen3.reasoner.rulesys.builtins.n3.BuiltinConfig;

public class BuiltinDefinitionSerializer extends BuiltinDefinitionMarshaller {

	private File serialFile;

	@Override
	// only call method in case of a custom definitions file
	public boolean marshallRequired(File definSrc, BuiltinConfig config) throws Exception {
		setSerialFile(definSrc);

		return (!serialFile.exists() || definSrc.lastModified() > serialFile.lastModified());
	}

	private void setSerialFile(File definSrc) {
		serialFile = new File(definSrc.getParentFile(), getId(definSrc) + ".bin");
	}

	@Override
	// only call method in case of a custom definitions file
	public void marshall(List<BuiltinDefinition> builtins, File definSrc, BuiltinConfig config) throws Exception {
		setSerialFile(definSrc);

		FileOutputStream f = new FileOutputStream(serialFile);
		ObjectOutputStream o = new ObjectOutputStream(f);

		o.writeObject(builtins);
		o.close();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<BuiltinDefinition> unmarshall(File definSrc, boolean custom, BuiltinConfig config) throws Exception {
		setSerialFile(definSrc);

		FileInputStream f = new FileInputStream(serialFile);
		ObjectInputStream o = new ObjectInputStream(f);

		List<BuiltinDefinition> builtins = (List<BuiltinDefinition>) o.readObject();
		builtins.stream().forEach(builtin -> {
			if (builtin.isInstantiate())
				try {
					builtin.setImpl(resolver.resolve(builtin));
					builtin.getInputConstraints().setBuiltin(builtin);

				} catch (Exception e) {
					e.printStackTrace();
				}
		});

		o.close();

		return builtins;
	}
}
