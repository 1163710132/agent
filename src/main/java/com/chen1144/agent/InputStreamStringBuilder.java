package com.chen1144.agent;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamStringBuilder {
    private InputStream inputStream;
    private ByteList builder;

    public InputStreamStringBuilder(InputStream inputStream){
        this.inputStream = inputStream;
        this.builder = new ByteList();
    }

    public byte[] readUntil(byte b) throws IOException {
        while (true){
            int read = inputStream.read();
            if(read == -1){
                return null;
            }
            if(read == b){
                byte[] result = builder.takeBytes();
                builder.clear();
                return result;
            }
            builder.put((byte) read);
        }
    }

    public byte[] readUntilAny(byte b0, byte b1) throws IOException{
        while (true){
            int read = inputStream.read();
            if(read == -1 || read == b0 || read == b1){
                return builder.takeBytes();
            }else{
                builder.put((byte) read);
            }
        }
    }

    public byte[] readUntil(byte b1, byte b2) throws IOException{
        int prev;
        int read = -1;
        while (true){
            prev = read;
            read = inputStream.read();
            if(read == -1){
                return null;
            }
            if(prev == b1 && read == b2){
                builder.pop();
                return builder.takeBytes();
            }else{
                builder.put((byte)read);
            }
        }
    }

    public byte[] read(int len) throws IOException{
        byte[] buffer = new byte[len];
        int offset = 0;
        while (offset < len){
            int count = inputStream.read(buffer, offset, len - offset);
            if(count == -1){
                break;
            }
            offset += count;
        }
        return buffer;
    }
}
