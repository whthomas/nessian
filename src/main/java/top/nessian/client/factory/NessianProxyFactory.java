package top.nessian.client.factory;

import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyResolver;
import com.caucho.hessian.io.*;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by whthomas on 15/11/12.
 */
public class NessianProxyFactory {

    private final ClassLoader loader;
    private NessianProxyResolver resolver;

    public NessianProxyFactory(){
        this(Thread.currentThread().getContextClassLoader());
    }

    public NessianProxyFactory(ClassLoader loader)
    {
        this.loader = loader;
        resolver = new NessianProxyResolver(this);
    }

    public Object create(Class api, String urlName)
            throws MalformedURLException
    {
        return null;
    }

    public Object create(Class<?> api, URL url, ClassLoader loader){
//        if (api == null)
//            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
//        InvocationHandler handler = null;
//
//        handler = new HessianProxy(url, this, api);
//
//        return Proxy.newProxyInstance(loader,
//                new Class[]{api,
//                        HessianRemoteObject.class},
//                handler);

        NessianProxy handler = new NessianProxy(api,url,this);


        return Proxy.newProxyInstance(loader,
                new Class[]{api,
                        HessianRemoteObject.class},
                handler);
    }

    public NessianClientFactory createNessianClientFactory(){
        return new NessianClientFactory();
    }

    public AbstractHessianInput getHessian2Input(InputStream is)
    {
        AbstractHessianInput in;

        in = new Hessian2Input(is);

//        in.setRemoteResolver(getRemoteResolver());
//
//        in.setSerializerFactory(getSerializerFactory());

        return in;
    }

}
