/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Logger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

/**
 * Load resources from the classpath.
 */
public abstract class MifosResourceUtil {

    private static final Logger LOGGER = Logger.getLogger(MifosResourceUtil.class.getName());
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    
    public static File getFile(String fileNameWithLocation) {
        File file;
        try {
            file = ResourceUtils.getFile(fileNameWithLocation);
        } catch (FileNotFoundException e) {
            throw new MifosRuntimeException("file not found :" + fileNameWithLocation, e);
        }
        return file;
    }

    private static File getFile(String location, String fileName) {
        return getFile(location + fileName);
    }

    public static Reader getSQLFileAsReader(String fileName) throws FileNotFoundException {
        return new FileReader(getFile("/sql/", fileName));
    }

    public static InputStream getSQLFileAsStream(String fileName) throws FileNotFoundException {
        return new FileInputStream(getFile("/sql/" + fileName));
    }

    /**
     * @deprecated Please replace usages of this method by e.g. {@link #getClassPathResourceAsURI(String)}, and remove this method ASAP.
     */
    @Deprecated
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"OS_OPEN_STREAM_EXCEPTION_PATH","OBL_UNSATISFIED_OBLIGATION"}, justification="this is a bug")
    public static File getClassPathResource(String fileName) throws IOException {
        URL url = getClassPathResourceAsResource(fileName).getURL();
        File file = null;
        if (url.getProtocol().equals(ResourceUtils.URL_PROTOCOL_FILE)) {
            file = getClassPathResourceAsResource(fileName).getFile();
        } else if (url.getProtocol().equals(ResourceUtils.URL_PROTOCOL_JAR)) {
            LOGGER.warning("Creating tmp file from InputSteam, use InputStream instead of file " + url);
            LOGGER.warning("Can not extract file from a jar in a container " + url);
            InputStream stream = getClassPathResourceAsStream(fileName);
            File jarResource = File.createTempFile("Mifos", ".xml");
            @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="")
            OutputStream out = new FileOutputStream(jarResource);
            byte buf[] = new byte[1024];
            int len;
            while ((len = stream.read(buf)) > 0) { // NOPMD by ugupta on 7/2/11 5:23 PM
                out.write(buf, 0, len);
            }
            out.close();
            stream.close();
            file = jarResource;
            LOGGER.warning("Created tmp file " + jarResource);
        }
        return file;
    }

    /**
     * Should be avoided, because an InputStream needs to be closed by somebody, more clear if caller creates the IS from a Resource.
     * Use {@link #getClassPathResourceAsResource(String)} instead.
     */
    public static InputStream getClassPathResourceAsStream(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    public static Resource getClassPathResourceAsResource(String fileName) throws IOException {
        return new ClassPathResource(fileName);
    }
    
    /**
     * Find a Resource on the Classpath and return it's URI (as a String).
     * 
     * Intended for calling code which accepts URI, such as the javax.xml APIs. Better than File-based API, which should
     * never be used. Better than InputStream, because an InputStream needs to be closed by somebody - and that's
     * usually forgotten.
     * 
     * If calling code can deal directly with a org.springframework.core.io.Resource, that's always preferable over a
     * raw URI; simply use new ClassPathResource(path) in that case.
     * 
     * @param path Path on the classpath, e.g. "org/mifos/something.xml"
     * @return a Stringified Classpath URI
     */
    public static String getClassPathResourceAsURI(String path) throws IOException {
        return new ClassPathResource(path).getURI().toString();
    }

    /**
     * @deprecated Please replace usages of this method by {@link #getClassPathResourceAsURI(String)}, and remove this method.
     */
    @Deprecated
    public static String getURI(String fileName) throws IOException {
        return getClassPathResourceAsURI(fileName);
    }

    /**
     * Returns all classpath resources matching a certain pattern.
     * 
     * @param pattern an Ant-style "/"-based (not "."-based) resource path pattern, WITHOUT any prefix 
     * (like "classpath:" or "classpath*:"); so e.g. "org/mifos/package/**&#47;*.xml".
     * @return an array of {@link Resource}. These may not actually be ClassPathResource instance, but any Resource (e.g. FileSystemResource)
     * @throws IOException in case of I/O errors
     */
    public static Resource[] getClassPathResourcesAsResources(String pattern) throws IOException {
        return resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/" + pattern);
        
    }
}
