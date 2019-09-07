package com.chen1144.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface HttpEntity {
    void writeTo(OutputStream outputStream) throws IOException;
    void readFrom(InputStream inputStream) throws IOException;
}
