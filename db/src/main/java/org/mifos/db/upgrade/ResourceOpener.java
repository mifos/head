/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.db.upgrade;

import liquibase.resource.ResourceAccessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

public class ResourceOpener implements ResourceAccessor, ResourceLoaderAware {
    private String changeLog;
    private ResourceLoader resourceLoader;

    public ResourceOpener(String changeLog) {
        this.changeLog = changeLog;
    }

    @Override
	public InputStream getResourceAsStream(String file) throws IOException {
        try {
            Resource resource = getResource(file);
            return resource.getInputStream();
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    @Override
	public Enumeration<URL> getResources(String packageName) throws IOException {
        Vector<URL> tmp = new Vector<URL>();

        tmp.add(getResource(packageName).getURL());

        return tmp.elements();
    }

    public Resource getResource(String file) {
        return resourceLoader.getResource(adjustClasspath(file));
    }

    private String adjustClasspath(String file) {
        return isClasspathPrefixPresent(changeLog) && !isClasspathPrefixPresent(file)
                ? ResourceLoader.CLASSPATH_URL_PREFIX + file
                : file;
    }

    public boolean isClasspathPrefixPresent(String file) {
        return file.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX);
    }

    @Override
	public ClassLoader toClassLoader() {
        return resourceLoader.getClassLoader();
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String getChangeLog() {
        return changeLog;
    }
}
