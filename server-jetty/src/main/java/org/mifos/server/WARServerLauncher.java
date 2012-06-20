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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    /**
     * Constructor.
     * 
     * @param httpPort HTTP Port
     * @param urlContext Web Context
     * @param warFile WAR Web Archive
     * @throws IllegalArgumentException if warFile is not a valid JAR (WAR) file, e.g. corrupt or technically valid but completely empty
     * @throws IOException if warFile could not even be found or opened for sanity checking
     */
    public WARServerLauncher(int httpPort, String urlContext, File warFile) throws IllegalArgumentException, IOException {
        super(httpPort, urlContext);
        this.warFile = warFile;
        checkArchive(warFile);
    }

    private void checkArchive(File jarFile) throws IOException, IllegalArgumentException {
    	if (jarFile.isDirectory())
    		return;
    	
		FileInputStream fis = new FileInputStream(jarFile);
		ZipInputStream zis = new JarInputStream(fis);
		ZipEntry e = zis.getNextEntry();
		zis.closeEntry();
		zis.close();
		fis.close();
		
		if (e == null)
			throw new IllegalArgumentException("This does not appear to be a valid JAR (WAR) file: " + jarFile);
	}

	@Override
    protected WebAppContext createWebAppContext() throws Exception {
        WebAppContext warCtx = new WebAppContext(warFile.toURI().toString(), "/" + getContext());

        if (warFile.isFile()) {
	        // http://mifosforge.jira.com/browse/MIFOS-4765
	        File warCtxTmpDir = new File(warFile.getParentFile(), warFile.getName().substring(0, warFile.getName().indexOf('.')));
	        IO.delete(warCtxTmpDir);
	        warCtx.setTempDirectory(warCtxTmpDir);
	        // DON'T warCtxTmpDir.deleteOnExit();
        
	        warCtx.setExtractWAR(true);
        } else {
        	// if warFile is a dir, it will be mifos/webapp - the temp is the one up: 
	        warCtx.setTempDirectory(warFile.getParentFile());
        }
        
        return warCtx;
    }

}
