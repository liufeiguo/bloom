package com.liu.netty;
 
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
 
public class TimeClient {
    public void connect(int port, String host) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHadler());
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
 
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
 
    public static void main(String[] args) {
        int port = 8080;
        try {
            new TimeClient().connect(port, "localhost");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	
        }
    }
}
