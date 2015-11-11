package top.nessian.server;

import com.caucho.hessian.io.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.nessian.server.annotation.NessianAPIScanner;
import top.nessian.server.model.HessianStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

    private static Logger logger = LoggerFactory.getLogger(NessianServerHandler.class);

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
        logger.info("调用完成,通道关闭.");
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 先获取 http Request 头信息
        if (msg instanceof HttpRequest) {

            HttpRequest request = (HttpRequest) msg;

            // 调用下面的method处理.
            this.dealRequestInfo(request, ctx);

        }

        // 之后会获得 http content信息
        if (msg instanceof HttpContent) {

            // 获得content
            HttpContent content = (HttpContent) msg;

            // 处理httpContent
            this.dealHttpContent(content, ctx);

        }

    }

    // 处理业务.

    /**
     * 处理Request请求.
     *
     * @param request
     * @param ctx
     * @throws Exception
     */
    public void dealRequestInfo(HttpRequest request, ChannelHandlerContext ctx) throws Exception {

        // 如果不是post请求,那么这个就不能成为hessian的接口.
        if (!request.getMethod().equals(HttpMethod.POST)) {
            throw new Exception();
        }

        // 通过URL选择method方法
        Method cacheMethod = NessianAPIScanner.methodMap.get(request.getUri());

        this.keepAlive = HttpHeaders.isKeepAlive(request);

        // 存入缓存对象.
        this.setMethod(cacheMethod);

        // 如果请求头中有含有 100,那么响应这个信息,让client继续发送请求.
        if (HttpHeaders.is100ContinueExpected(request)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
    }

    /**
     * 处理 HttpContent 的method
     *
     * @param content
     * @param ctx
     * @throws IOException
     */
    public void dealHttpContent(HttpContent content, ChannelHandlerContext ctx) throws IOException {

        // 得到一个字节容器ByteBuf,这里的这个ByteBuf是操作直接内存的.
        ByteBuf directBuf = content.content();

        // 转换成数组
        int length = directBuf.readableBytes();
        byte[] datas = new byte[length];
        directBuf.getBytes(directBuf.readerIndex(), datas);

        // 构建一个响应的response
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

        // 使用语法糖自动关闭流
        try (// 形成一个InputStream
             InputStream inputStream = new ByteArrayInputStream(datas);
             // 把response的内容放到一个OutputStream中
             OutputStream outputStream = new ByteBufOutputStream(response.content())) {

            // 选取hessian调用的模式
            HessianStream hessianStream = selectCallType(inputStream, outputStream);

            AbstractHessianInput in = hessianStream.getHessianInput();
            AbstractHessianOutput out = hessianStream.getHessianOutput();

            // 设置序列化工厂
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
            Object[] values = new Object[args.length];

            // 读取传过来的参数.
            for (int i = 0; i < args.length; i++) {
                values[i] = in.readObject(args[i]);
            }

            Object result = null;

            try {
                result = method.invoke(method.getDeclaringClass().newInstance(), values);
            } catch (Exception e) {
                e.printStackTrace();
                // 处理异常
            }

            // 写入结果到out流中.
            out.writeReply(result);
        }

        // 声明response的信息
        response.headers().set(CONTENT_TYPE, "x-application/hessian");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        // 判断是否构建长连接.并根据此响应信息.
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            ctx.write(response);
        }
    }

    // 判断请求的格式
    public HessianStream selectCallType(InputStream inputStream, OutputStream outputStream) throws IOException {

        // 读取头信息,判断hessian的调用格式.
        HessianInputFactory.HeaderType header = inputFactory.readHeader(inputStream);

        // header type is CALL_1_REPLY_2,这里还要做匹配
        AbstractHessianInput in;
        AbstractHessianOutput out;

        // 根据格式创建hessian序列化的方式.
        switch (header) {
            case CALL_1_REPLY_1:
                in = hessianFactory.createHessianInput(inputStream);
                out = hessianFactory.createHessianOutput(outputStream);
                break;

            case CALL_1_REPLY_2:
                in = hessianFactory.createHessianInput(inputStream);
                out = hessianFactory.createHessian2Output(outputStream);
                break;

            case HESSIAN_2:
                in = hessianFactory.createHessian2Input(inputStream);
                in.readCall();
                out = hessianFactory.createHessian2Output(outputStream);
                break;

            default:
                throw new IllegalStateException(header + " is an unknown Hessian call");
        }

        return new HessianStream(in, out);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理发生异常,通道关闭.", cause);
        ctx.close();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
