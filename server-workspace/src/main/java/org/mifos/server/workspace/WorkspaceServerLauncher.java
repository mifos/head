/*
 * Copyright (c) 2011 Grameen Foundation USA
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

package org.mifos.server.workspace;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.mifos.server.AbstractServerLauncher;

/**
 * Jetty-based all-classpath web application starter.
 *
 * @see https://sites.google.com/site/michaelvorburger/simpleservers
 *
 * @author Michael Vorburger
 */
public class WorkspaceServerLauncher extends AbstractServerLauncher {

    private static final String WEB_INF_WEB_XML = "WEB-INF/web.xml";

    public WorkspaceServerLauncher(int httpPort, String urlContext) {
        super(httpPort, urlContext);
    }

    @Override
    protected WebAppContext createWebAppContext() throws Exception {
        final WebContextWithServletContextResourceExtension webAppContext;
        webAppContext = new WebContextWithServletContextResourceExtension(null, "/" + getContext());

        final ResourceCollection baseResources = baseResources();
        if (baseResources != null) {
            // This is if a web.xml
            webAppContext.setBaseResource(baseResources);
        } else {
            // This is if there is no web.xml, only META-INF/resources & web-fragment.xml
            final File tempFileDir = File.createTempFile("jetty-empty-context" , Long.toString(System.nanoTime()));
            IO.delete(tempFileDir);
            tempFileDir.mkdirs();
            tempFileDir.deleteOnExit();
            webAppContext.setBaseResource(Resource.newResource(tempFileDir.toURI()));
        }
        webAppContext.replaceConfiguration(MetaInfConfiguration.class, new MetaInfFolderConfiguration());
        webAppContext.replaceConfiguration(FragmentConfiguration.class, new FragmentFolderConfiguration());
        webAppContext.replaceConfiguration(WebInfConfiguration.class, new WebInfFolderExtendedConfiguration());

        //For JNDI
        webAppContext.addConfiguration(new PlusConfiguration());
        webAppContext.addConfiguration(new EnvConfiguration());

        // This will make EVERYTHING on the classpath be
        // scanned for META-INF/resources and web-fragment.xml - great for dev!
        // NOTE: Several patterns can be listed, separate by comma
        webAppContext.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*");
        webAppContext.setAttribute(WebInfConfiguration.WEBINF_JAR_PATTERN, ".*");

        // Needed for http://mifosforge.jira.com/browse/MIFOS-4918
        File tmp = new File("./targetEclipse/jetty-work");
        IO.delete(tmp);
        tmp.mkdirs();
        webAppContext.setTempDirectory(tmp);

        return webAppContext;
    }

    private ResourceCollection baseResources() throws IOException, MalformedURLException {
        final List<Resource> webResourceModules = new LinkedList<Resource>();
        final URL webXml = webXmlUrl();
        if (webXml != null) {
            webResourceModules.add(Util.chop(webXml, WEB_INF_WEB_XML));
        }
        if (!webResourceModules.isEmpty()) {
            return new ResourceCollection(webResourceModules.toArray(new Resource[webResourceModules.size()]));
        } else {
            return null;
        }
    }

    /**
     * Finds the web.xml
     *
     * @return URL of the web.xml on the Classpath
     */
    private static URL webXmlUrl() throws IOException {
        final Collection<URL> urls = getResources(WEB_INF_WEB_XML);
        if (urls.isEmpty()) {
            return null; // Nope... throw new IllegalStateException(WEB_INF_WEB_XML + " not found on the classpath");
        }
        if (urls.size() != 1) {
            throw new IllegalStateException(WEB_INF_WEB_XML + " was found more than once on the classpath: "
                    + urls.toString());
        }
        return urls.iterator().next();
    }

    private static Collection<URL> getResources(String resource) throws IOException {
        final ClassLoader cl = WorkspaceServerLauncher.class.getClassLoader(); // OR
        // Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> urls = cl.getResources(resource);
        final LinkedList<URL> list = new LinkedList<URL>();
        while (urls.hasMoreElements()) {
            list.add(urls.nextElement());
        }
        return list;
    }

}
