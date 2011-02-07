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

package org.mifos.server;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebInfConfiguration;

/**
 * Sample Jetty-based all-classpath web application starter.
 * 
 * @see https://sites.google.com/site/michaelvorburger/simpleservers
 * 
 * @author Michael Vorburger
 */
public class ServerLauncher {

    private static final String WEB_INF_WEB_XML = "WEB-INF/web.xml";

    private final int port;
    private final String context;

    private Server server;
    private WebContextWithExtraConfigurations webAppContext;

    public ServerLauncher() {
        this(8080);
    }

    public ServerLauncher(int httpPort) {
        this(httpPort, "mifos");
    }

    public ServerLauncher(int httpPort, String urlContext) {
        this.port = httpPort;
        this.context = urlContext;
    }

    public void startServer() throws Exception {
        if (server != null) {
            throw new IllegalStateException("HTTP Server already running, stop it first before starting it again");
        }
        server = new Server();

        final SocketConnector connector = new SocketConnector();
        connector.setPort(port);
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        server.setConnectors(new Connector[] { connector });

        webAppContext = new WebContextWithServletContextResourceExtension(null, "/" + context);
        final ResourceCollection baseResources = baseResources();
        if (baseResources != null) {
            // This is if a web.xml
            webAppContext.setBaseResource(baseResources);
        } else {
            // This is if there is no web.xml, only META-INF/resources & web-fragment.xml
            final File tempFileDir = File.createTempFile("jetty-empty-context-for-" + context + "__",
                    Long.toString(System.nanoTime()));
            tempFileDir.delete();
            tempFileDir.mkdir();
            webAppContext.setBaseResource(Resource.newResource(tempFileDir.toURI()));
        }
        webAppContext.setLogUrlOnStart(true);
        // webAppContext.setParentLoaderPriority(true); // not needed...
        // webAppContext.setCompactPath(true);
        webAppContext.setServer(server);
        webAppContext.getServletHandler().setStartWithUnavailable(false); // this is great: if WAR couldn't start, don't
                                                                          // swallow, but propagate!
        server.setHandler(webAppContext);

        // webAppContext.setTempDirectory(...);

        webAppContext.replaceConfiguration(MetaInfConfiguration.class, new MetaInfFolderConfiguration());
        webAppContext.replaceConfiguration(FragmentConfiguration.class, new FragmentFolderConfiguration());
        webAppContext.replaceConfiguration(WebInfConfiguration.class, new WebInfFolderExtendedConfiguration());

        // This will make EVERYTHING on the classpath be
        // scanned for META-INF/resources and web-fragment.xml - great for dev!
        // NOTE: Several patterns can be listed, separate by comma
        webAppContext.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*");
        webAppContext.setAttribute(WebInfConfiguration.WEBINF_JAR_PATTERN, ".*");

        // No sure how much use that is, as we'll terminate this via Ctrl-C, but it doesn't hurt either:
        server.setStopAtShutdown(true);

        server.start();

        if (!webAppContext.isAvailable() || webAppContext.isFailed() || !webAppContext.isRunning()
                || !webAppContext.isStarted() || server.isFailed() || server.isFailed() || !server.isRunning()
                || !server.isStarted()) {
            // We must (try to) STOP the server, otherwise the forked background
            // thread keeps running, and the JVM won't exit (e.g. in JUnit
            // Tests)
            server.stop();
            if (webAppContext.getUnavailableException() != null) {
                throw new IllegalStateException(
                        "Web App in Jetty Server does not seem to have started up; CHECK THE LOG! PS: Chained exception is: ",
                        webAppContext.getUnavailableException());
            } else {
                throw new IllegalStateException(
                        "Web App in Jetty Server does not seem to have started up; CHECK THE LOG! (NO chained exception)");
            }
        }
    }

    public void stopServer() throws Exception {
        webAppContext.stop();
        webAppContext = null;
        server.stop();
        server = null;
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
        final ClassLoader cl = ServerLauncher.class.getClassLoader(); // OR
                                                                      // Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> urls = cl.getResources(resource);
        final LinkedList<URL> list = new LinkedList<URL>();
        while (urls.hasMoreElements()) {
            list.add(urls.nextElement());
        }
        return list;
    }

    public int getPort() {
        return this.port;
    }

    public String getContext() {
        return this.context;
    }
}
