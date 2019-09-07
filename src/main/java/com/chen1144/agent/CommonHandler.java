package com.chen1144.agent;

import com.chen1144.agent.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.function.Function;

import static com.chen1144.agent.Constants.HOST;

public class CommonHandler implements Handler {
    @Override
    public boolean filter(HttpRequest message) {
        return true;
    }

    @Override
    public boolean handle(HttpRequest request, Socket clientSocket, List<StringPair> map) throws IOException {
        Utils.preHandle(request, map);
        String host = request.url.toString();
        Socket serverSocket = Utils.connect(host);
        InputStream server2Proxy = serverSocket.getInputStream();
        OutputStream proxy2Server = serverSocket.getOutputStream();
        InputStream client2Proxy = clientSocket.getInputStream();
        OutputStream proxy2Client = clientSocket.getOutputStream();
        request.writeTo(proxy2Server);
        proxy2Server.flush();
        Thread thread2Client = new Thread(()->{
            try {
                Utils.pipe(client2Proxy, proxy2Server);
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        Thread thread2Server = new Thread(()->{
            try {
                Utils.pipe(server2Proxy, proxy2Client);
            }catch (IOException e){
                e.printStackTrace();
            }
        });
        thread2Client.start();
        thread2Server.start();
        try {
            thread2Client.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return false;
    }
}
