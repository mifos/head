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

package org.mifos.reports;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.io.FileUtils;
import org.eclipse.birt.report.listener.ViewerServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Servlet Context Listener for BIRT Resources.
 * 
 * This copies classpath*:/birt/** (from inside the mifos-reporting-*.jar in the WAR) on start-up into a temporary
 * directory and sets the SYS_PROP_BIRT_ROOT_TEMP_DIR (mifos.birt.root.temp.dir) to that absolute location on the
 * filesystem, allowing BIRT to find it's *.rptdesign etc. via the same placeholder in web-fragment.xml.
 * 
 * @see http://mifosforge.jira.com/browse/MIFOS-5089
 * 
 * @author Michael Vorburger
 */
public class MifosViewerServletContextListener extends ViewerServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MifosViewerServletContextListener.class);

    public static final String SYS_PROP_BIRT_ROOT_TEMP_DIR = "mifos.birt.root.temp.dir";

    private static final String BIRT_RESOURCES_PATTERN = "/birt/";
    private static final String LOCATION_PATTERN = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + BIRT_RESOURCES_PATTERN + "**";

    /**
     * Sets the System Property IBirtConstants.SYS_PROP_ROOT_PATH to a temporary directory into which the *.rptdesign
     * files etc. are extracted into from the classpath, before invoking the parent ViewerServletContextListener's
     * {@link ViewerServletContextListener#contextInitialized(ServletContextEvent)}.
     * 
     * @throws RuntimeException if BIRT_RESOURCES_PATTERN not found on Classpath
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        File birtRootDir = copyBIRTResourcesFromClassPathToFilesystemDirectory(event.getServletContext());
        System.setProperty(SYS_PROP_BIRT_ROOT_TEMP_DIR, birtRootDir.getAbsolutePath());
        LOGGER.info("System Property " + SYS_PROP_BIRT_ROOT_TEMP_DIR + " set to " + birtRootDir);
        super.contextInitialized(event);
    }

    /* package-local */
    File copyBIRTResourcesFromClassPathToFilesystemDirectory(ServletContext servletContext) {
        File directory = new File(System.getProperty("java.io.tmpdir"), "MifosBirtFilesExtractedFromClasspath");
        try {
            directory.mkdirs();
            FileUtils.cleanDirectory(directory);
            copyFromClassPathToDirectory(BIRT_RESOURCES_PATTERN, directory);
        } catch (IOException e) {
            error(servletContext, "getResources(\"" + ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + BIRT_RESOURCES_PATTERN + "**\") failed: " + e.getMessage(), e);
        }

        return directory;
    }

    private void copyFromClassPathToDirectory(String directoryToScan, File rootDirectory) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(LOCATION_PATTERN);
        LOGGER.info("Found " + resources.length + " Resources on " + LOCATION_PATTERN);
        for (Resource resource : resources) {
            if (resource.exists() & resource.isReadable() && resource.contentLength() > 0) {
                URL url = resource.getURL();
                String urlString = url.toExternalForm();
                String targetName = urlString.substring(urlString.indexOf(directoryToScan));
                File destination = new File(rootDirectory, targetName);
                FileUtils.copyURLToFile(url, destination);
                LOGGER.info("Copied " + url + " to " + destination.getAbsolutePath());
            } else {
                LOGGER.debug("Did not copy, seems to be directory: " + resource.getDescription());
            }
        }
    }

    private void error(ServletContext servletContext, String msg, Exception e) throws RuntimeException {
        LOGGER.error(msg, e);
        if (servletContext != null) {
            servletContext.log(msg, e);
        }
        throw new RuntimeException(msg, e);
    }
}
