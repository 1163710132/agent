package com.chen1144.agent;

import com.chen1144.agent.util.Pair;
import com.chen1144.agent.util.Property;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chen1144.agent.Constants.*;

public class HttpResponse {
    public AsciiString version;
    public AsciiString statement;
    public AsciiString phrase;
    public List<Pair<AsciiString,AsciiString>> headerFields;
    public HttpEntity entity;
    private boolean available;

    public HttpResponse(){
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

    public boolean isAvailable(){
        return available;
    }

    public HttpResponse readFrom(InputStream inputStream) throws IOException {
        InputStreamStringBuilder builder = new InputStreamStringBuilder(inputStream);
        byte[] methodBytes = builder.readUntil(NBSP);
        if(methodBytes == null){
            available = false;
            return this;
        }
        version = new AsciiString(methodBytes);
        statement = new AsciiString(builder.readUntil(NBSP));
        phrase = new AsciiString(builder.readUntil(CR, LF));
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
        if(DEBUG){
            if(entity instanceof ChunkedEntity){
                entity.writeTo(System.out);
            }
        }
        return this;
    }

    public HttpResponse writeTo(OutputStream outputStream) throws IOException{
        outputStream.write(version.toByteArray());
        outputStream.write(NBSP);
        outputStream.write(statement.toByteArray());
        outputStream.write(NBSP);
        outputStream.write(phrase.toByteArray());
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

    public static HttpResponse connectResponse(AsciiString protocol){
        HttpResponse response = new HttpResponse();
        response.version = protocol;
        response.phrase = AsciiString.cached("Connection Established");
        response.statement = AsciiString.cached("200");
        //response.headerFields = List.of(new StringPair(PROXY_AGENT, "Netscape-Proxy/1.1"));
        response.headerFields = List.of();
        response.entity = null;
        return response;
    }
}
