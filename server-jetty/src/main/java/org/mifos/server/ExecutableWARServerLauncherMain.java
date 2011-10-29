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

    public ExecutableWARServerLauncherMain(int httpPort, String urlContext) throws IOException {
        super(httpPort, urlContext, getWARFile());
    }

    private static File getWARFile() throws IOException {
        final String classResourceName = ExecutableWARServerLauncherMain.class.getName().replace('.', '/') + ".class";
        URL url = ExecutableWARServerLauncherMain.class.getClassLoader().getResource(classResourceName);
        if (url == null)
            throw new IOException("URL for class resource not found: " + classResourceName);
        File warFile = new File(((JarURLConnection) url.openConnection()).getJarFile().getName());
        System.out.println("WAR File is " + warFile);
        return warFile.getAbsoluteFile();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length > 1) {
            throw new IllegalArgumentException("Why more than one arg? USAGE: [HTTP-Port], max. 1 (optional) argument");
        }

        final ExecutableWARServerLauncherMain serverLauncher = new ExecutableWARServerLauncherMain(port, "mifos");
        serverLauncher.startServer();
    }

}
