package top.nessian.client;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

import java.io.InputStream;

/**
 * Created by whthomas on 15/11/11.
 */
public class NessianClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private InputStream inputStream;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {

        if (httpObject instanceof HttpResponse) {

        }

        if (httpObject instanceof HttpContent){

            HttpContent content = (HttpContent) httpObject;

            inputStream = new ByteBufInputStream(content.content());

        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
