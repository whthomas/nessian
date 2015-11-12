package top.nessian.client.api;

import top.nessian.client.annotation.NessianClient;

/**
 * Created by whthomas on 15/10/26.
 */
public interface BasicAPI {
    @NessianClient("/test")
    String hello(String word);
}
