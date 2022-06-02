package wvw.utils.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TreeMultiMap<K, V> extends TreeMap<K, List<V>> implements MultiMap<K, V> {

	private static final long serialVersionUID = 1L;

	public TreeMultiMap() {
	}

	public TreeMultiMap(Map<K, List<V>> map) {
		super(map);
	}

	@SuppressWarnings("unchecked")
	public void putValue(K key, V... values) {
		putValue(key, false, values);
	}

	@SuppressWarnings("unchecked")
	public void putValue(K key, boolean ignoreDuplicates, V... values) {
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
