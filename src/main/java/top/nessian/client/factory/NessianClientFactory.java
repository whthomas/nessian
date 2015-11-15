package top.nessian.client.factory;

/**
 * Created by whthomas on 15/11/12.
 */
public class NessianClientFactory {

    private String scheme;
    private String host;
    private String port;
    private String path;

    public NessianClientFactory() {
    }

    // 构建
    public NessianClientFactory(String scheme, String host,
                                String port, String path) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
    }

//    public Class<T> create(Class<T> c, String url) {
//
//    }

}
