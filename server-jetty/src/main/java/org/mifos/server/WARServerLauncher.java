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

import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * WAR-based Jetty-based web application starter.
 * 
 * Jetty is on the Classpath here.
 * Web Application is not on the classpath here, File-based WAR must be given as argument.
 * 
 * @author Michael Vorburger
 */
public class WARServerLauncher extends AbstractServerLauncher {

    private final File warFile;

    public WARServerLauncher(int httpPort, String urlContext, File warFile) {
        super(httpPort, urlContext);
        this.warFile = warFile;
    }

    @Override
    protected WebAppContext createWebAppContext() throws Exception {
        WebAppContext warCtx = new WebAppContext(warFile.toURI().toString(), "/" + getContext());

        // http://mifosforge.jira.com/browse/MIFOS-4765
        File warCtxTmpDir = new File(warFile.getParentFile(), warFile.getName() + "_tmp");
        IO.delete(warCtxTmpDir);
        warCtx.setTempDirectory(warCtxTmpDir);
        warCtxTmpDir.deleteOnExit();
        
        warCtx.setExtractWAR(true);
        
        return warCtx;
    }

}
