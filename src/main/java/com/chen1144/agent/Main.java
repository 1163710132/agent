package com.chen1144.agent;

import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

public class Main {
    public static void main(String[] args) {
        Agent agent = new Agent();
        agent.addHandler(new CommonHandler());
        agent.addHandler(new ConnectHandler());
        agent.addHandler(new GetHandler());
        agent.bind(4396);
        agent.redirectList.add(new StringPair("www.dilidili.wang", "www.hit.edu.cn"));
        agent.blockList.add("www.4399.com");
        agent.start();
    }
}
