package top.nessian.client.service;

import top.nessian.server.annotation.NessianAPI;
import top.nessian.server.annotation.NessianAPIScanner;

import java.util.Map;

/**
 * Created by whthomas on 15/12/3.
 */
public class UrlServiceImpl implements UrlService {

    @NessianAPI("/all/urls")
    public Map getAllUrl() {
        return NessianAPIScanner.methodToUrlMap;
    }

}
