package top.nessian.server.annotation;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import top.nessian.client.service.UrlService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by whthomas on 15/11/10.
 */
public class NessianAPIScanner {

    // 方法缓存器.
    public static final Map<String, Method> methodMap = new HashMap<>();
    public static final Map<String, String> methodToUrlMap = new HashMap<>();

    public static void scanner(String scannerPackageName) {

        // 构建查找器
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(scannerPackageName))
                .setScanners(new MethodAnnotationsScanner(), new TypeAnnotationsScanner()));

        // 选出所有带有 @NessianAPI 注解的方法
        Set<Method> methods = reflections.getMethodsAnnotatedWith(NessianAPI.class);

        Method urlMethod = null;
        try {
            urlMethod = UrlService.class.getMethod("getAllUrl");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        // 加入获取URL的api
        methodToUrlMap.put("top.nessian.client.service.UrlService.getAllUrl", "/all/urls");
        methodMap.put("/all/urls", urlMethod);

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

            String fullMethodName = String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());

            methodToUrlMap.put(fullMethodName, url);

            // 把方法和URL对应起来,然后缓存起来.
            methodMap.put(url, method);

        }
    }
}
