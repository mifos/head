package org.mifos.platform.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class ClassUtils {

    public static Iterable<Class<?>> getClasses(String packageName, String endsWith) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile())); // NOPMD by ugupta on 11/29/11 7:25 PM
        }
        List<Class<?>> classes = new LinkedList<Class<?>>();
        for (File directory : dirs) {
            findClasses(directory, packageName, endsWith, classes);
        }
        return classes;
    }

    public static void findClasses(File directory, String packageName, String endsWith, List<Class<?>> classes) throws ClassNotFoundException {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                findClasses(file, packageName + "." + file.getName(), endsWith, classes);
            } else if (file.getName().endsWith(endsWith + ".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
    }
}
