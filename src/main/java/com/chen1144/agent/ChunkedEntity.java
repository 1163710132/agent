package com.chen1144.agent;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.chen1144.agent.Constants.*;
import static com.chen1144.agent.HttpRequest.*;

public class ChunkedEntity implements HttpEntity {
    private List<byte[]> byteList;

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        for(var bytes : byteList){
            outputStream.write(new AsciiString(Integer.toString(bytes.length, 16)).toByteArray());
            outputStream.write(CRLF.toByteArray());
            outputStream.write(bytes);
            outputStream.write(CRLF.toByteArray());
            outputStream.flush();
        }
        outputStream.write(AsciiString.cached("0").toByteArray());
        outputStream.write(CRLF.toByteArray());
        outputStream.write(CRLF.toByteArray());
    }

    @Override
    public void readFrom(InputStream inputStream) throws IOException {
        InputStreamStringBuilder builder = new InputStreamStringBuilder(inputStream);
        byteList = new ArrayList<>();
        while (true){
            byte[] countString = builder.readUntil(CR, LF);
            int count = new AsciiString(countString).parseInt(16);
            if(count == -1){
                return;
            }
            if(count == 0){
                break;
            }
            byte[] bytes = builder.read(count);
            builder.readUntil(CR, LF);
            byteList.add(bytes);
        }
        builder.readUntil(CR, LF);
    }
}
