package top.nessian.server.annotation;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by whthomas on 15/11/10.
 */
public class NessianAPIScanner {

    public static final Map<String, Method> methodMap = new HashMap<>();

    public static void scanner(String scannerPackageName) {

        // 构建查找器
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(scannerPackageName))
                .setScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner()));

        // 选出所有带有 @NessianAPI 注解的方法
        Set<Method> methods = reflections.getMethodsAnnotatedWith(NessianAPI.class);

        // 遍历所有带有注解的方法,然后操作.
        for (Method method : methods) {

            String url = method.getAnnotation(NessianAPI.class).value();

            // 校验URL格式
            if (!url.startsWith("/")) {
                url += "/";
            }

            // 检查URL是不是重复了.
            if (methodMap.containsKey(url)) {
                throw new RuntimeException("url有重复.");
            }

            // 把方法和URL对应起来,然后缓存起来.
            methodMap.put(url, method);
        }
    }
}
