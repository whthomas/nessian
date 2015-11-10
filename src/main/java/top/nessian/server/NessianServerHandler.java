package top.nessian.server;

import com.caucho.hessian.io.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import top.nessian.server.annotation.NessianAPIScanner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by whthomas on 15/11/7.
 */
public class NessianServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final SerializerFactory serializerFactory = new SerializerFactory();
    private static final HessianFactory hessianFactory = new HessianFactory();
    private static final HessianInputFactory inputFactory = new HessianInputFactory();

//    private CompositeByteBuf compositeByteBuf = ByteBufAllocator.compositeBuffer();

    // 存方法
    private Method method;
    // 存是否是长连接
    private boolean keepAlive;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 先获取 http Request 头信息
        if (msg instanceof HttpRequest) {

            HttpRequest request = (HttpRequest) msg;

            // 通过URL选择method方法
            Method cacheMethod = NessianAPIScanner.methodMap.get(request.getUri());

            this.keepAlive = HttpHeaders.isKeepAlive(request);

            // 存入缓存对象.
            this.setMethod(cacheMethod);

            // 如果请求头中有含有 100,那么响应这个信息,让client继续发送请求.
            if (HttpHeaders.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }

            // 如果不是post请求,那么这个就不能成为hessian的接口.
            if (!request.getMethod().equals(HttpMethod.POST)){
                throw new Exception();
            }

        }

        // 之后会获得 http content信息
        if (msg instanceof HttpContent) {

            HttpContent content = (HttpContent)msg;

            ByteBuf directBuf = content.content();

            int length = directBuf.readableBytes();

            byte[] datas = new byte[length];

            directBuf.getBytes(directBuf.readerIndex(), datas);

            InputStream inputStream = new ByteArrayInputStream(datas);

            HessianInputFactory.HeaderType header = inputFactory.readHeader(inputStream);

            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

            OutputStream outputStream = new ByteBufOutputStream(response.content());

            // header type is CALL_1_REPLY_2,这里还要做匹配
            AbstractHessianInput in = hessianFactory.createHessianInput(inputStream);
            AbstractHessianOutput out = hessianFactory.createHessian2Output(outputStream);

            in.setSerializerFactory(serializerFactory);

            in.skipOptionalCall();

            String headers;
            while ((headers = in.readHeader()) != null) {
                Object value = in.readObject();
            }

            // 取出参数类型数组
            Class<?> args[] = this.getMethod().getParameterTypes();

            // 获取传来的方法名称
            String methodName = in.readMethod();

            // 计算传来的数组长度.
            int argLength = in.readMethodArgLength();

            // 构建对象
            Object []values = new Object[args.length];

            // 读取传过来的参数.
            for (int i = 0; i < args.length; i++) {
                values[i] = in.readObject(args[i]);
            }

            Object result = null;

            try {
                result = method.invoke(method.getDeclaringClass().newInstance(),values);
            } catch (Exception e) {
                e.printStackTrace();
                // 处理异常
            }

            out.writeReply(result);
            out.close();
            in.close();

//            response.content().writeBytes();

            response.headers().set(CONTENT_TYPE, "x-application/hessian");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                ctx.write(response);
            }

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("处理发生异常,通道关闭.");
        ctx.close();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
}
