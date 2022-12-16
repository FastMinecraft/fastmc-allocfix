package dev.fastmc.allocfix;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DummyLinkedHashMap<K, V> extends LinkedHashMap<K, V> implements Object2ObjectMap<K, V> {
    private final Object2ObjectMap<K, V> delegate;

    public DummyLinkedHashMap(Object2ObjectMap<K, V> delegate) {
        super(0);
        this.delegate = delegate;
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public V put(K k, V v) {
        return delegate.put(k, v);
    }

    @Override
    public V remove(Object k) {
        return delegate.remove(k);
    }

    @Override
    public V get(Object k) {
        return delegate.get(k);
    }

    @Override
    public boolean containsKey(Object k) {
        return delegate.containsKey(k);
    }

    @Override
    public boolean containsValue(Object v) {
        return delegate.containsValue(v);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public ObjectSet<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public ObjectCollection<V> values() {
        return delegate.values();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public ObjectSet<Map.Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public ObjectSet<Entry<K, V>> object2ObjectEntrySet() {
        return delegate.object2ObjectEntrySet();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void defaultReturnValue(V rv) {
        delegate.defaultReturnValue(rv);
    }

    @Override
    public V defaultReturnValue() {
        return delegate.defaultReturnValue();
    }
}
