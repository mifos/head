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
import java.net.JarURLConnection;
import java.net.URL;

import org.mifos.server.tray.MifosTray;


/**
 * Main class.
 *
 * This is the Jetty Server Starter used in the "Executable WAR".
 *
 * It expects to be run from within an archive which has WAR file
 * directory layout, and having the Jetty JARs and this code extracted
 * "over" the standard WAR (NOT within WEB-INF/lib).
 *
 * @author Michael Vorburger
 */
public class ExecutableWARServerLauncherMain extends WARServerLauncher {
	// If this class is ever renamed or refactored, please change the ${exec.war.main.class} property in the parent pom.xml
	
	private static File warFile;
	
    public ExecutableWARServerLauncherMain(int httpPort, String urlContext) throws IOException {
        super(httpPort, urlContext, getWARFileOrDirectory(), getTempDir());
    }

    private static File getWARFileOrDirectory() throws IOException {
    	// This (temp/ + "webapp") is hard-coded in org.eclipse.jetty.webapp.WebInfConfiguration#unpack as well, so fair game:
    	File extractedWebAppDir = new File(getTempDir(), "webapp");
    	if (extractedWebAppDir.exists() && extractedWebAppDir.isDirectory()) {
    		System.out.println("WAR directory found (already unpacked previously) : " + extractedWebAppDir);
    		return extractedWebAppDir;
    	} else {
    		System.out.println("WAR directory NOT found: " + extractedWebAppDir);
    		return getWARFile();
    	}
    }
    
    private static File getWARFile() throws IOException {
    	if (warFile == null) {
	        final String classResourceName = ExecutableWARServerLauncherMain.class.getName().replace('.', '/') + ".class";
	        URL url = ExecutableWARServerLauncherMain.class.getClassLoader().getResource(classResourceName);
	        if (url == null)
	            throw new IOException("URL for class resource not found: " + classResourceName);
	        warFile = new File(((JarURLConnection) url.openConnection()).getJarFile().getName()).getAbsoluteFile();
	        System.out.println("WAR Archive File found (it will be unpacked automatically by Jetty on first launch if the temporary directory doesn't exist already): " + warFile);
    	}
    	return warFile;
    }

    private static File getTempDir() throws IOException {
    	// Place the temp/ directory right "next" to the WAR file, for now
		File warFile = getWARFile();
		File tempDir = new File(warFile.getParentFile(), "temp");
		// Pre-create temp/, because if it's not, then Jetty will deleteOnExit it
		tempDir.mkdirs();
		return tempDir;
	}
    
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            throw new IllegalArgumentException("Why more than one arg? USAGE: [HTTP-Port], max. 1 (optional) argument");
        }

        final ExecutableWARServerLauncherMain serverLauncher = new ExecutableWARServerLauncherMain(port, "mifos");

		final MifosTray tray = new MifosTray(serverLauncher.getAppURL(), "Mifos.log") {
			@Override
			public void quit() {
				// First stop the server
				try {
					serverLauncher.stopServer();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				// Then remove the tray icon
				super.quit();
			}
		};
		
		tray.init();
		try {
			serverLauncher.startServer();
			tray.started(true);
		} catch (Exception e) {
			// Just for end-user convenience, open log if the server didn't start up properly (e.g. DB unreachable)
			tray.openLog();
			// If the server didn't start up properly, we MUST shutdown the Tray,
			// otherwise there are some lingering AT Daemon threads, and the JVM doesn't quit.
			tray.quit();
			throw e;
		}
    }

}
