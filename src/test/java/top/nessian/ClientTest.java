package top.nessian;

import com.caucho.hessian.client.HessianProxyFactory;
import top.nessian.server.api.BasicAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by whthomas on 15/10/27.
 */
public class ClientTest {
    public static void main(String args[]) throws IOException {

        String url = "http://localhost:8080/bingo";
//        String url = "http://localhost:8080";

        HessianProxyFactory factory = new HessianProxyFactory();
        BasicAPI basic = null;
        try {
            basic = (BasicAPI) factory.create(BasicAPI.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        InputStream inputStream = Files.newInputStream(Paths.get("/Users/whthomas/development/dev_test/ssqian/d.png"));

        System.out.println("hello(): " + basic.bingo("bingo!"));

    }
}
