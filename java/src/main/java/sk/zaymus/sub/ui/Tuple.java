package sk.zaymus.sub.ui;

/**
 * Class is designed for storing pair of 2 values. Values are set at object
 * creation and can not be changed afterwards.
 *
 *
 * @author Mikulas Zaymus
 * @param <K> key data type
 * @param <V> value data type
 */
public class Tuple<K, V> {

    private final K key;
    private final V value;

    /**
     *
     * @param key key object
     * @param value value object
     */
    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     *
     * @return key object
     */
    public K getKey() {
        return key;
    }

    /**
     *
     * @return value object
     */
    public V getValue() {
        return value;
    }

}
