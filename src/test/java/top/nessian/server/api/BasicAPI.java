package top.nessian.server.api;

import java.io.InputStream;

/**
 * Created by whthomas on 15/10/26.
 */
public interface BasicAPI {
    String hello(String word,InputStream data);

    Vo bingo(Vo vo);

}
