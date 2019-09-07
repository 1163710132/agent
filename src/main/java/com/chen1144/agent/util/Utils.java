package com.chen1144.agent.util;

import com.chen1144.agent.HttpRequest;
import com.chen1144.agent.StringPair;
import com.chen1144.agent.Tuple;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chen1144.agent.Constants.HOST;
import static com.chen1144.agent.Constants.HTTP_PORT;

public abstract class Utils {
    public static final Charset ASCII = Charset.forName("ASCII");

    public static String encode(byte[] bytes){
        CharBuffer charBuffer = ASCII.decode(ByteBuffer.wrap(bytes));
        return new String(charBuffer.array());
    }

    public static void pipe(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[256];
        while (true){
            int len = inputStream.read(buffer);
            if(len != -1){
                //System.out.println(encode(buffer));
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }else{
                break;
            }
        }
    }

    public static Socket connect(String host){
        try {
            Socket serverSocket = new Socket();
            String[] split = host.split(":");
            int port = HTTP_PORT;
            if(split.length > 1){
                port = Integer.parseInt(split[1]);
            }
            serverSocket.connect(new InetSocketAddress(InetAddress.getByName(split[0]), port));
            return serverSocket;
        }catch (IOException e){
            //throw new RuntimeException(e);
            //throw e;
            throw new RuntimeException("Connection timeout" + host);
        }
    }

    public static String regexReplace(String string, List<StringPair> context){
        for(StringPair pair : context){
            string = string.replaceAll(pair.key, pair.value);
        }
        return string;
    }

    public static void preHandle(HttpRequest request, List<StringPair> context){
        Property<AsciiString> hostProperty = request.getHeader(HOST);
        String oldHost = hostProperty.getValue().toString();
        String oldUrl = request.url.toString();
        String newHost = regexReplace(oldHost, context);
        String newUrl = regexReplace(oldUrl, context);
        request.url = new AsciiString(newUrl);
        hostProperty.setValue(new AsciiString(newHost));
    }

    public static String getAndClear(StringBuilder builder){
        String value = builder.toString();
        builder.setLength(0);
        return value;
    }
}
