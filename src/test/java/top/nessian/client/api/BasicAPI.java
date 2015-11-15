package top.nessian.client.api;

import top.nessian.client.annotation.NessianClientAPI;

/**
 * Created by whthomas on 15/10/26.
 */
public interface BasicAPI {
    @NessianClientAPI("/test")
    String hello(String word);
}
