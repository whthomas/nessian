package top.nessian;

import com.caucho.hessian.client.HessianProxyFactory;
import top.nessian.server.api.BasicAPI;

import java.net.MalformedURLException;

/**
 * Created by whthomas on 15/10/27.
 */
public class ClientTest {
    public static void main(String args[]) {

        String url = "http://localhost:8080/test";
//        String url = "http://localhost:8080";

        HessianProxyFactory factory = new HessianProxyFactory();
        BasicAPI basic = null;
        try {
            basic = (BasicAPI) factory.create(BasicAPI.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println("hello(): " + basic.hello("bingo!"));

    }
}
