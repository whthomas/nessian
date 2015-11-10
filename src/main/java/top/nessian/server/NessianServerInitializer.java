package top.nessian.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by whthomas on 15/11/7.
 */
public class NessianServerInitializer  extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        // 获取管道
        ChannelPipeline p = socketChannel.pipeline();

        // 添加加解码
        p.addLast(new HttpServerCodec());

        // 自动压缩数据
        p.addLast(new HttpContentDecompressor());
        p.addLast(new NessianServerHandler());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
