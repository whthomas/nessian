package top.nessian.business;

import top.nessian.server.annotation.NessianAPI;

/**
 * Created by whthomas on 15/11/10.
 */

public class BasicAPIb {

    @NessianAPI("/yrl")
    public String hello(){
        return "bingo";
    }

}
