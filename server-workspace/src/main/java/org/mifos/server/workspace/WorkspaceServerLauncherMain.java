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

/**
 * Main class.
 * 
 * This is a simple Server Starter, and useful e.g. within Eclipse.
 * 
 * It expects Jetty and the Application to be on classpath (not packaged in a WAR).
 * 
 * @author Michael Vorburger
 */
public class WorkspaceServerLauncherMain {

    // Very simply for now; could later read a conf/server.properties, set to tmp/, configure a logs/ etc.

    public static void main(String[] args) throws Exception {
        final int port = Integer.parseInt(args[0]);
        final WorkspaceServerLauncher serverLauncher = new WorkspaceServerLauncher(port, "mifos");
        serverLauncher.startServer();
    }
}
