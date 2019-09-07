package com.chen1144.agent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Handler {
    boolean filter(HttpRequest message);
    boolean handle(HttpRequest request, Socket clientSocket, List<StringPair> regexList) throws IOException;
}
