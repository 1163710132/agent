package com.chen1144.agent;

import io.netty.util.AsciiString;

import java.nio.ByteBuffer;

public abstract class Constants {
    public static final AsciiString HOST = AsciiString.cached("Host");
    public static final byte COLON = ':';
    public static final int HTTP_PORT = 80;
    public static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    public static final AsciiString CONNECTION = AsciiString.cached("Connection");
    public static final AsciiString CLOSE = AsciiString.cached("close");
    public static final AsciiString TRANSFER_ENCODING = AsciiString.cached("Transfer-Encoding");
    public static final AsciiString CHUNKED = AsciiString.cached("chunked");
    public static final AsciiString GET = AsciiString.cached("GET");
    public static final AsciiString HTTP = AsciiString.cached("HTTP");
    public static final AsciiString POST = AsciiString.cached("POST");
    public static final AsciiString CONNECT = AsciiString.cached("CONNECT");
    public static final AsciiString IF_MODIFIED_SINCE = AsciiString.cached("If-Modified-Since");
    public static final byte NBSP = ' ';
    public static final byte CR = '\r';
    public static final byte LF = '\n';
    public static final AsciiString CRLF = AsciiString.cached("\r\n");
    public static final AsciiString PROXY_AGENT = AsciiString.cached("Proxy-agent");
    public static final boolean DEBUG = false;
    public static final AsciiString STMT304 = AsciiString.cached("304");
    public static final AsciiString STMT200 = AsciiString.cached("200");
}
