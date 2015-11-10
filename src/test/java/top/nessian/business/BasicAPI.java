package top.nessian.business;

import top.nessian.server.annotation.NessianAPI;

/**
 * Created by whthomas on 15/11/10.
 */

public class BasicAPI implements test.whthomas.BasicAPI {

    @NessianAPI("/hessian/ihessian")
    public String hello(String world) {
        return "hello"+world;
    }

}
