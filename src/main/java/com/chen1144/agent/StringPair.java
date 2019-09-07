package com.chen1144.agent;

public class StringPair {
    public String key;
    public String value;

    public StringPair(String key, String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return key.hashCode() + value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StringPair){
            StringPair pair = (StringPair)obj;
            return pair.key.equals(key) && pair.value.equals(value);
        }
        return false;
    }
}
