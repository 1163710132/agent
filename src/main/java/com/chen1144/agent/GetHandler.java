package com.chen1144.agent;

import com.chen1144.agent.util.Property;
import com.chen1144.agent.util.Utils;
import io.netty.handler.codec.DateFormatter;
import io.netty.util.AsciiString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static com.chen1144.agent.Constants.*;

public class GetHandler implements Handler {
    private final Map<String, Tuple<Date, HttpResponse>> cache;
    private AtomicInteger cacheHit;
    //private final Map<String, Queue<Socket>> connections;

    public GetHandler(){
        cache = new ConcurrentHashMap<>();
        cacheHit = new AtomicInteger(0);
        //connections = new ConcurrentHashMap<>();
    }

    @Override
    public boolean filter(HttpRequest message) {
        return message.method.equals(GET);
    }

    private Socket borrowConnection(String host){
        Socket socket;//= connections.computeIfAbsent(host, useLess->new ConcurrentLinkedQueue<>()).poll();
        //if(socket == null || socket.isClosed()){
            socket = Utils.connect(host);
        //    connections.getValue(host).add(socket);
        //}
        return socket;
    }

    private void returnConnection(String host, Socket socket){
        try {
            socket.close();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        //connections.getValue(host).add(socket);
    }

    @Override
    public boolean handle(HttpRequest request, Socket clientSocket, List<StringPair> mapper) throws IOException {
        InputStream client2Proxy = clientSocket.getInputStream();
        OutputStream proxy2Client = clientSocket.getOutputStream();
        Utils.preHandle(request, mapper);
        String host = request.getHeader(HOST).getValue().toString();
        String replacedHost = host;
        var serverClientSupplier = new Supplier<Socket>(){
            private Socket socket;
            private String host = replacedHost;
            @Override
            public Socket get() {
                if(socket == null){
                    socket = borrowConnection(host);
                }
                return socket;
            }
            public void close(){
                if(socket != null){
                    returnConnection(host, socket);
                }
                socket = null;
            }
        };
        try {
            HttpRequest newRequest = request;
            while (true){
                if(newRequest.isAvailable()){
                    Utils.preHandle(request, mapper);
                    HttpResponse response;
                    if(request.url.length() > 100){
                        response = getResponseWithoutCache(newRequest, serverClientSupplier);
                    }else{
                        response = getResponseWithCache(newRequest, serverClientSupplier);
                    }
                    response.writeTo(proxy2Client);
                    if(Optional.ofNullable(request.getHeader(CONNECTION))
                            .map(Property::getValue)
                            .filter(CLOSE::equals)
                            .isPresent()){
                        break;
                    }
                    if(Optional.ofNullable(response.getHeader(CONNECTION))
                            .map(Property::getValue)
                            .filter(CLOSE::equals)
                            .isPresent()){
                        break;
                    }
                }else{
                    break;
                }
                newRequest = new HttpRequest();
                newRequest.readFrom(client2Proxy);
            }
        }catch (IOException e) {
            System.err.print(host);
            System.err.println(request.url);
            throw e;
        }finally {
            serverClientSupplier.close();
        }
        return false;
    }

    public HttpResponse requestResponse(HttpRequest request, Socket serverConnection) throws IOException{
        HttpResponse response = new HttpResponse();
        request.writeTo(serverConnection.getOutputStream());
        response.readFrom(serverConnection.getInputStream());
        return response;
    }

    public HttpResponse getResponseWithoutCache(HttpRequest request, Supplier<Socket> socketSupplier) throws IOException{
        return requestResponse(request, socketSupplier.get());
    }

    public HttpResponse getResponseWithCache(HttpRequest request, Supplier<Socket> socketSupplier) throws IOException{
        Property<AsciiString> hostProperty = request.getHeader(HOST);
        String hostString = hostProperty.getValue().toString();
        String id = hostString + request.url;
        Property<AsciiString> ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE);
        Tuple<Date, HttpResponse> cached = cache.get(id);
        if(cached != null){
            if(ifModifiedSince == null){
                Date requestDate = cached.getFirst();
                request.addHeader(IF_MODIFIED_SINCE, new AsciiString(DateFormatter.format(requestDate)));
                HttpResponse response = requestResponse(request, socketSupplier.get());
                System.out.println(response.statement);
                if(response.statement.equals(STMT304)){
                    //update date, use cache
                    cached.setFirst(Date.from(Instant.now()));
                    System.err.println("Cache hit: " + id);
                    return cached.getSecond();
                }else if(response.statement.equals(STMT200)){
                    //update date and response
                    cached.setFirst(Date.from(Instant.now()));
                    cached.setSecond(response);
                    System.err.println("Cache updated: " + id);
                }
                return response;
            }else{
                Date requestDate = DateFormatter.parseHttpDate(ifModifiedSince.getValue());
                HttpResponse response = requestResponse(request, socketSupplier.get());
                System.out.println(response.statement);
                if(response.statement.equals(STMT200)){
                    if(requestDate.after(cached.getFirst())){
                        //update date and response
                        cached.setFirst(requestDate);
                        System.err.println("Cache updated: " + id);
                    }
                    cached.setFirst(Date.from(Instant.now()));
                }
                return response;
            }
        }else{
            HttpResponse response = requestResponse(request, socketSupplier.get());
            //System.out.println(response.statement);
            Date requestDate;
            requestDate = (ifModifiedSince == null)
                    ? Date.from(Instant.now())
                    : DateFormatter.parseHttpDate(ifModifiedSince.getValue());
            if(response.statement.equals(STMT200)){
                cached = new Tuple<>(requestDate, response);
                cache.put(id, cached);
                System.out.println("Cached: " + id);
            }
            return response;
        }
    }
}
