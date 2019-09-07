package com.chen1144.agent;

import java.util.*;

public class HttpHeaderMap implements Map<String, String> {
    private List<String> keys;
    private List<String> values;

    public HttpHeaderMap(){
        keys = new ArrayList<>();
        values = new ArrayList<>();
    }

    private int indexOfKey(Object key){
        return keys.indexOf(key);
    }

    private int indexOfKey(String key){
        return keys.indexOf(key);
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    @Override
    public String get(Object key) {
        int index = keys.indexOf(key);
        return index == -1 ? null : values.get(index);
    }

    @Override
    public String put(String key, String value) {
        int index = keys.indexOf(key);
        if(index == -1){
            keys.add(key);
            values.add(value);
            return null;
        }else{
            return values.set(index, value);
        }
    }

    @Override
    public String remove(Object key) {
        int index = keys.indexOf(key);
        if(index == -1){
            return null;
        }else{
            keys.remove(index);
            return values.remove(index);
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        keys.clear();
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return new Set<Entry<String, String>>() {
            @Override
            public int size() {
                return HttpHeaderMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return HttpHeaderMap.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Entry<String, String>> iterator() {
                return new Iterator<Entry<String, String>>() {
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < HttpHeaderMap.this.size();
                    }

                    @Override
                    public Entry<String, String> next() {
                        return new MapEntry(index++);
                    }
                };
            }

            @Override
            public Object[] toArray() {
                return toArray(new Object[HttpHeaderMap.this.size()]);
            }

            @Override
            public <T> T[] toArray(T[] a) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean add(Entry<String, String> stringStringEntry) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends Entry<String, String>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
        };
    }
    class MapEntry implements Entry<String, String>{
        private int index;

        public MapEntry(int index){
            this.index = index;
        }

        @Override
        public String getKey() {
            return keys.get(index);
        }

        @Override
        public String getValue() {
            return values.get(index);
        }

        @Override
        public String setValue(String value) {
            return values.set(index, value);
        }
    }
}
