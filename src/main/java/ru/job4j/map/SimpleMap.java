package ru.job4j.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SimpleMap<K, V> implements Map<K, V> {

    private static final float LOAD_FACTOR = 0.75f;
    private int capacity = 8;
    private int count = 0;
    private int modCount = 0;
    private MapEntry<K, V>[] table = new MapEntry[capacity];

    @Override
    public boolean put(K key, V value) {
        if (capacity * LOAD_FACTOR <= count) {
            expand();
        }
        boolean res = false;
        int index = index(key);
        if (table[index] == null) {
            table[index] = new MapEntry<>(key, value);
            res = true;
            modCount++;
            count++;
        }
        return res;
    }

    private int hash(int hashCode) {
        return hashCode ^ (hashCode >>> 16);
    }

    private int indexFor(int hash) {
        return hash & (capacity - 1);
    }

    private void expand() {
        capacity *= 2;
        MapEntry<K, V>[] oldTable = table;
        table = new MapEntry[capacity];
        for (MapEntry<K, V> entry: oldTable) {
            if (entry != null) {
                int index = index(entry.key);
                table[index] = entry;
            }
        }
    }

    @Override
    public V get(K key) {
        MapEntry<K, V> entry = key == null ? table[0] : table[index(key)];
        return entry != null
                && Objects.hashCode(key) == Objects.hashCode(entry.key)
                && Objects.equals(key, entry.key)
                ? entry.value : null;
    }

    @Override
    public boolean remove(K key) {
        boolean res = false;
        int index = index(key);
        if (table[index] != null
                && Objects.hashCode(key) == Objects.hashCode(table[index].key)
                && Objects.equals(table[index].key, key)) {
            table[index] = null;
            modCount++;
            count--;
            res = true;
        }
        return res;
    }

    private int index(K key) {
        return indexFor(hash(Objects.hashCode(key)));
    }

    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            final int expandModCount = modCount;
            int index;

            @Override
            public boolean hasNext() {
                if (modCount != expandModCount) {
                    throw new ConcurrentModificationException();
                }
                while (index < capacity && table[index] == null) {
                    index++;
                }
                return index < capacity;
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return table[index++].key;
            }
        };
    }

    private static class MapEntry<K, V> {
        K key;
        V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}