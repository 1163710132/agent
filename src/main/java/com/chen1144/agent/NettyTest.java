package com.chen1144.agent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyTest {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelHandler() {
                                @Override
                                public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {

                                }

                                @Override
                                public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {
                                    channelHandlerContext.flush();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                                    channelHandlerContext.close();
                                    throw new RuntimeException(throwable);
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(4396).sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
