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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Jetty-based web application starter.
 * 
 * @author Michael Vorburger
 */
public abstract class AbstractServerLauncher {

    private final int port;
    private final String context;

    private Server server;
    private WebAppContext webAppContext;

    public AbstractServerLauncher(int httpPort, String urlContext) {
        this.port = httpPort;
        this.context = urlContext;
    }

    protected abstract WebAppContext createWebAppContext() throws Exception;

    public void startServer() throws Exception {
        server = createServer();

        webAppContext = createWebAppContext();

        webAppContext.setLogUrlOnStart(true);
        // webAppContext.setParentLoaderPriority(true); // not needed...
        // webAppContext.setCompactPath(true);
        webAppContext.setServer(server);
        // this is great: if WAR couldn't start, don't swallow cause, but propagate!
        webAppContext.getServletHandler().setStartWithUnavailable(false);
        server.setHandler(webAppContext);

        // webAppContext.setTempDirectory(...);

        // No sure how much use that is, as we'll terminate this via Ctrl-C, but
        // it doesn't hurt either:
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

    protected Server createServer() {
        if (server != null) {
            throw new IllegalStateException("HTTP Server already running, stop it first before starting it again");
        }
        server = new Server();

        final SocketConnector connector = new SocketConnector();
        connector.setPort(port);
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        server.setConnectors(new Connector[] { connector });
        
        return server;
    }

    public void stopServer() throws Exception {
        webAppContext.stop();
        webAppContext = null;
        server.stop();
        server = null;
    }

    public int getPort() {
        return this.port;
    }

    public String getContext() {
        return this.context;
    }

    /**
     * Application base URL.
     *
     * @return String of App's URL, including a trailing slash after the context.
     */
    public String getAppURL() {
        return "http://localhost:" + getPort() + "/" + getContext() + "/";
    }

}
