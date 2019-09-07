package com.chen1144.agent;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import static com.chen1144.agent.Constants.*;

public class Agent {
    private ServerSocket listener;
    private Thread serverThread;
    private List<Handler> handlers;
    public final List<StringPair> redirectList;
    public final List<String> blockList;

    public Agent(){
        handlers = new ArrayList<>();
        serverThread = new Thread(()->{
            try {
                while (true){
                    Socket clientSocket = listener.accept();
                    new Thread(()->{
                        try {
                            while (true){
                                boolean succeed = handleClientSocket(clientSocket);
                                if(!succeed){
                                    break;
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            try {
                                clientSocket.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
        redirectList = new Vector<>();
        blockList = new Vector<>();
    }

    public void addHandler(Handler handler){
        handlers.add(handler);
    }

    private boolean handleClientSocket(Socket clientSocket) throws IOException{
        InputStream client2Proxy = clientSocket.getInputStream();
        HttpRequest request = new HttpRequest();
        request.readFrom(client2Proxy);
        if(request.isAvailable()){
            if(DEBUG){
                request.writeTo(System.out);
            }
            if(blockList.stream().anyMatch(request.getHeader(HOST).getValue()::matches)){
                return false;
            }
            Handler handler = null;
            for(int i = handlers.size();i > 0;i--){
                handler = handlers.get(i - 1);
                if(handler.filter(request)){
                    break;
                }
            }
            if(handler != null){
                return handler.handle(request, clientSocket, redirectList);
            }
        }
        return false;
    }

    public void bind(int port){
        if(listener == null){
            try {
                listener = new ServerSocket(port, 50, Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}));
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    public void start(){
        serverThread.start();
    }

}
