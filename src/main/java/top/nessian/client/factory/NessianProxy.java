package top.nessian.client.factory;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import top.nessian.client.NessianClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by whthomas on 15/11/12.
 */
public class NessianProxy implements InvocationHandler {

    private Class<?> type;
    private URL url;
    protected NessianProxyFactory factory;

    public NessianProxy(Class<?> type, URL url, NessianProxyFactory factory) {
        this.type = type;
        this.url = url;
        this.factory = factory;
    }








    private SerializerFactory serializerFactory;

    // 调用
    public Object invoke(Object proxy, Method method, Object args[]) {

        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();

        InputStream is = null;

        return null;
    }

    // send httpRequest
    public AbstractHessianOutput sendRequest(String methodName, Object[] args)
            throws IOException, URISyntaxException, InterruptedException {

        OutputStream os = new ByteArrayOutputStream();

//        AbstractHessianOutput out = _factory.getHessianOutput(os);
        AbstractHessianOutput output;

//        if (_isHessian2Request)
//            out = new Hessian2Output(os);
//        else {
//            HessianOutput out1 = new HessianOutput(os);
//            out = out1;
//
//            if (_isHessian2Reply)
//                out1.setVersion(2);
//        }

        HessianOutput out = new HessianOutput(os);

        serializerFactory = new SerializerFactory();

        out.setSerializerFactory(serializerFactory);

        out.call(methodName, args);
        out.flush();

        // 发送真正的请求
        NessianClient.sendRequest("");

        // 拿到InputStream,然后解码给调用的方法.

        return out;
    }

}
