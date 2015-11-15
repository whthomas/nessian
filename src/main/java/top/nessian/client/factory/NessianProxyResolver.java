package top.nessian.client.factory;

import java.io.IOException;

/**
 * Created by whthomas on 15/11/13.
 */
public class NessianProxyResolver {

    private NessianProxyFactory factory;

    public NessianProxyResolver(NessianProxyFactory factory) {
        this.factory = factory;
    }

    public Object lookup(String type, String url)
            throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class api = Class.forName(type, false, loader);
            return factory.create(api, url);
        } catch (Exception e) {
            throw new IOException(String.valueOf(e));
        }
    }
}
