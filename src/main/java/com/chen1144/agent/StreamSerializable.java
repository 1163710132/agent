package com.chen1144.agent;

import java.io.InputStream;
import java.io.OutputStream;

public interface StreamSerializable {
    void writeToStream(OutputStream outputStream);
    void readFromStream(InputStream inputStream);
}
