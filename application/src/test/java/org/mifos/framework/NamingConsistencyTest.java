/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

public class NamingConsistencyTest {

    /**
     * test Consistency of test's name according to surefire-maven-plugin settings <br>
     * <br>
     * see <a href=http://www.mifos.org/developers/wiki/MavenIntegrationTests>
     * http://www.mifos.org/developers/wiki/MavenIntegrationTests</a>
     */
    @Test
    public void integrationTestsNameCheck() throws ClassNotFoundException, IOException {
        for (Class<?> clazz : getClasses("org.mifos", "Test")) {
            String clazzName = clazz.getName();
            if (!clazzName.endsWith("IntegrationTest") && !clazzName.endsWith("StrutsTest")) {
                if (clazz.getSuperclass().equals(MifosIntegrationTestCase.class)
                        || clazz.getSuperclass().equals(MifosMockStrutsTestCase.class)) {
                    fail(clazz + " extends integration test cases MifosIntegrationTestCase or MifosMockStrutsTestCase");
                }
            }
        }
    }

    private static Class<?>[] getClasses(String packageName, String endsWith) throws ClassNotFoundException,
            IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName, endsWith, classes));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName, String endsWith,
            ArrayList<Class<?>> classes) throws ClassNotFoundException {
        if (!directory.exists()) {
            return classes;
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
        return classes;
    }
}
