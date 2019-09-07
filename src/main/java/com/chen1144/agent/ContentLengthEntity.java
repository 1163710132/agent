package com.chen1144.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ContentLengthEntity implements HttpEntity {
    private byte[] bytes;

    public ContentLengthEntity(int length){
        bytes = new byte[length];
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(bytes);
    }

    @Override
    public void readFrom(InputStream inputStream) throws IOException {
        int off = 0;
        while (off < bytes.length){
            int readCount = inputStream.read(bytes, off, bytes.length - off);
            if(readCount == -1){
                break;
            }
            off += readCount;
        }
    }
}
