package wvw.utils.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMultiMap<K, V> extends HashMap<K, List<V>> implements MultiMap<K, V> {

	private static final long serialVersionUID = 1L;

	public HashMultiMap() {
	}
	
	public HashMultiMap(Map<K, List<V>> map) {
		super(map);
	}

	@SafeVarargs
	public final void putValue(K key, V... values) {
		putValue(key, false, values);
	}

	@SafeVarargs
	public final void putValue(K key, boolean ignoreDuplicates, V... values) {
		List<V> elements = get(key);

		if (elements == null) {
			elements = new ArrayList<V>();

			put(key, elements);
		}

		for (V value : values) {

			if (!ignoreDuplicates || !elements.contains(value))
				elements.add(value);
		}
	}

	public void removeEl(K key, V value) {
		List<V> elements = get(key);
		if (elements == null)
			return;

		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).equals(value)) {
				elements.remove(i);

				break;
			}
		}

		if (elements.isEmpty())
			remove(key);
	}
}
