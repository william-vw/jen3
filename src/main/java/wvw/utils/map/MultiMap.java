package wvw.utils.map;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface MultiMap<K, V> extends Map<K, List<V>>, Serializable {

	@SuppressWarnings("unchecked")
	public void putValue(K key, V... value);


	@SuppressWarnings("unchecked")
	public void putValue(K key, boolean ignoreDuplicates, V... value);

	public void removeEl(K key, V value);
}
