package com.chen1144.agent;

import com.chen1144.agent.util.Pair;
import com.chen1144.agent.util.Property;
import com.chen1144.agent.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chen1144.agent.Constants.*;
import static com.chen1144.agent.util.Utils.*;

public class HttpRequest {
    public AsciiString method;
    public AsciiString url;
    public AsciiString version;
    public List<Pair<AsciiString, AsciiString>> headerFields;
    public HttpEntity entity;
    private boolean available;

    public HttpRequest(){
        this.available = false;
    }

    public Property<AsciiString> getHeader(AsciiString key){
        for(var pair : headerFields){
            if(pair.getKey().equals(key)){
                return pair;
            }
        }
        return null;
    }

    public Property<AsciiString> addHeader(AsciiString key, AsciiString value){
        var pair = new Pair<>(key, value);
        headerFields.add(pair);
        return pair;
    }

    public boolean isAvailable(){
        return available;
    }

    public HttpRequest readFrom(InputStream inputStream) throws IOException {
        InputStreamStringBuilder builder = new InputStreamStringBuilder(inputStream);
        byte[] methodBytes = builder.readUntil(NBSP);
        if(methodBytes == null){
            available = false;
            return this;
        }
        method = new AsciiString(methodBytes);
        url = new AsciiString(builder.readUntil(NBSP));
        version = new AsciiString(builder.readUntil(CR, LF));
        headerFields = new ArrayList<>();
        while (true){
            AsciiString key = new AsciiString(builder.readUntilAny((byte)':', CR));
            if(key.length() == 0){
                builder.readUntil(LF);
                break;
            }
            builder.readUntil(NBSP);
            AsciiString value = new AsciiString(builder.readUntil(CR, LF));
            headerFields.add(new Pair<>(key, value));
        }
        Property<AsciiString> property;
        if((property = getHeader(CONTENT_LENGTH)) != null){
            entity = new ContentLengthEntity(property.getValue().parseInt());
        }else if((property = getHeader(TRANSFER_ENCODING)) != null){
            if(property.getValue().equals(CHUNKED)){
                entity = new ChunkedEntity();
            }
        }
        if(entity != null){
            entity.readFrom(inputStream);
        }
        available = true;
        return this;
    }

    public HttpRequest writeTo(OutputStream outputStream) throws IOException{
        outputStream.write(method.toByteArray());
        outputStream.write(NBSP);
        outputStream.write(url.toByteArray());
        outputStream.write(NBSP);
        outputStream.write(version.toByteArray());
        outputStream.write(CRLF.toByteArray());
        for(var pair : headerFields){
            outputStream.write(pair.getKey().toByteArray());
            outputStream.write(COLON);
            outputStream.write(NBSP);
            outputStream.write(pair.getValue().toByteArray());
            outputStream.write(CRLF.toByteArray());
        }
        outputStream.write(CRLF.toByteArray());
        if(entity != null){
            entity.writeTo(outputStream);
        }
        outputStream.flush();
        return this;
    }
}
