package top.nessian.client;

import com.caucho.hessian.io.*;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by whthomas on 15/11/28.
 */
public class NessianProxy implements MethodInterceptor {

    private SerializerFactory serializerFactory = new SerializerFactory();

    /**
     * invoke the real method in remote server
     *
     * @return
     */
    public Object invoke(String url, Method method, Object args[]) {

        Class<?>[] params = method.getParameterTypes();

        // 创建一个HTTP Client
        CloseableHttpClient client = HttpClients.createDefault();

        // 构建服务商的URL
        HttpPost post = new HttpPost(url);

        post.addHeader(HttpHeaders.HOST, "127.0.0.1");
        post.addHeader(HttpHeaders.CONNECTION, "close");
        post.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");

        post.addHeader("Content-Type", "x-application/hessian");
        post.addHeader("Accept-Encoding", "deflate");

        // Prepare the HTTP request, Hessian use POST request.

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput out1 = new HessianOutput(os);
        AbstractHessianOutput out = out1;
        out1.setVersion(2);

        out.setSerializerFactory(serializerFactory);

        try {
            out.call(method.getName(), args);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 加入序列化过的请求.
        post.setEntity(new ByteArrayEntity(os.toByteArray()));

        // deal response data
        try {
            CloseableHttpResponse response = client.execute(post);

            HttpEntity outEntity = response.getEntity();

            InputStream inputStream = outEntity.getContent();

            AbstractHessianInput in;

            int code = inputStream.read();

            if (code == 'H') {
                int major = inputStream.read();
                int minor = inputStream.read();

                in = new Hessian2Input(inputStream);

//                in.setRemoteResolver(getRemoteResolver());

                in.setSerializerFactory(serializerFactory);

                Object value = in.readReply(method.getReturnType());

                return value;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy methodProxy) throws Throwable {

        System.out.println("远程调用开始.");

        NessianProxy nessianProxy = new NessianProxy();

        Object object = nessianProxy.invoke("http://127.0.0.1:8080/bingo", method, args);

        System.out.println(object);
        System.out.println("远程调用结束.");

        return object;
    }

}
