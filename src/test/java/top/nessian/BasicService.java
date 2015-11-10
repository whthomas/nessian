package top.nessian;

import com.caucho.hessian.server.HessianServlet;

/**
 * Created by whthomas on 15/10/26.
 */
public class BasicService extends HessianServlet implements BasicAPI {
    private String _greeting = "Hello, world";

    public void setGreeting(String greeting)
    {
        _greeting = greeting;
    }

    public String hello(String word)
    {
        return "say"+ word;
    }
}
