package top.nessian.server.api;

import java.io.Serializable;

/**
 * Created by whthomas on 15/12/3.
 */
public class Vo implements Serializable{

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
