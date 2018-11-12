package com.epam.jvmmagic.black;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.*;

public class CustomClassloaders {

    public static void main(String[] args) {
        Map<String, List<Object>> items = new HashMap<>();
        for (int requestId = 0; requestId < 2; requestId++) {
            //TODO: try to increase the applications count and investigate how it impacts the resources usage
            for (int appId = 0; appId < 3; appId++) {
                String req = "req" + requestId;
                Object response = request(req, "app" + appId, "profile" + appId);
                items.computeIfAbsent(req, key -> new ArrayList<>()).add(response);
            }
        }
        System.out.println(items);
        validateResponses(items);
    }

    private static Object request(String requestId, String appName, String profile) {
        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                    getModuleURL("classloaders.api"),
                    //TODO: try to exclude this module and instead list in the pom file as shared dependency
                    getModuleURL("classloaders.cache")
            });
            Class<?> apiClass = classLoader.loadClass("com.epam.jvmmagic.MyApi");
            Method init = apiClass.getDeclaredMethod("init", String.class, String.class);
            init.invoke(null, appName, profile);
            Method request = apiClass.getDeclaredMethod("request", String.class);
            return request.invoke(null, requestId);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void validateResponses(Map<String, List<Object>> responses) {
        responses.forEach((request, requestResponses) -> {
            Object first = null;
            for (Object response : requestResponses) {
                if (first == null) {
                    first = response;
                } else if (!Objects.equals(first, response)) {
                    System.out.println("req " + request + ":" + first + " not equal to " + response);
                }
            }
        });
    }

    private static URL getModuleURL(String moduleName) throws MalformedURLException {
        return Paths.get(moduleName, "target", moduleName + "-1.0-SNAPSHOT.jar").toAbsolutePath().normalize().toUri().toURL();
    }
}
