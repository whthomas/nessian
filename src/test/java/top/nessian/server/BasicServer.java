package top.nessian.server;

import org.apache.commons.io.IOUtils;
import top.nessian.server.annotation.NessianAPI;
import top.nessian.server.api.BasicAPI;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by whthomas on 15/11/12.
 */
public class BasicServer implements BasicAPI {

    @NessianAPI("/test")
    @Override
    public String hello(String word,InputStream inputStream) {

        try {
            byte[] data = IOUtils.toByteArray(inputStream);

            Files.write(Paths.get("/Users/whthomas/development/dev_test/ssqian/x.png"),data);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return word;
    }

    @NessianAPI("/bingo")
    @Override
    public String bingo(String luck) {
        return luck;
    }
}
