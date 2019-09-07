package com.chen1144.agent.util;

public interface Property<T> {
    void setValue(T value);
    T getValue();
}
