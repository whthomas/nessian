package top.nessian.client;

import com.caucho.hessian.io.AbstractHessianInput;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by whthomas on 15/11/11.
 */
public class NessianClient {

    private InputStream inputStream;
    private InputStream errorStream;

    public static void sendRequest(String url) throws URISyntaxException, InterruptedException {

        // 构建URL
        URI uri = new URI(url);

        // 获取协议
        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        // 获取主机名称
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        // 获取端口号
        int port = uri.getPort();
        //如果端口号无法识别出来.
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NessianClientInitializer());

            // Make the connection attempt.
            ChannelFuture channelFuture = b.connect(host, port).sync();
            Channel ch = channelFuture.channel();

            // Prepare the HTTP request, Hessian use POST request.
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

            // Set some example cookies.
//            request.headers().set(
//                    HttpHeaders.Names.COOKIE,
//                    ClientCookieEncoder.encode(
//                            new DefaultCookie("my-cookie", "foo"),
//                            new DefaultCookie("another-cookie", "bar")));

//            ch.pipeline().

            // 定义当请求完成的时候,触发的事件
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    // 先获取到那个Handler
                    NessianClientHandler channelHandler =
                            (NessianClientHandler)future.channel().pipeline().get("nessianClient");
                    // 获取response回来的hessian格式的数据InputStream
                    InputStream is = channelHandler.getInputStream();

                    AbstractHessianInput in;

                    try {
                        int code = is.read();

                        if (code == 'H') {
                            int major = is.read();
                            int minor = is.read();

//                            in = _factory.getHessian2Input(is);
//
//                            Object value = in.readReply(method.getReturnType());
//
//                            return value;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }

    }

}
