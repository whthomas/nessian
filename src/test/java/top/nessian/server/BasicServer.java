package top.nessian.server;

import top.nessian.server.annotation.NessianAPI;
import top.nessian.server.api.BasicAPI;

/**
 * Created by whthomas on 15/11/12.
 */
public class BasicServer implements BasicAPI {

    @NessianAPI("/test")
    @Override
    public String hello(String word) {
        return word;
    }
}
