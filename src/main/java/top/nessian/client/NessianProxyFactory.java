package top.nessian.client;

import net.sf.cglib.proxy.Enhancer;

/**
 * Created by whthomas on 15/12/2.
 */
public class NessianProxyFactory {

    /**
     * 创建要被代理的class
     *
     * @param target
     * @return
     */
    public static Object create(Class target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target);
        // 回调方法
        enhancer.setCallback(new NessianProxy());
        // 创建代理对象
        return enhancer.create();
    }

}
