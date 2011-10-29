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

package org.mifos.server.wartestexec;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

/**
 * Test launching a packaged Mifos Executable WAR. The Mifos Application *AND* the Jetty web container are contained
 * within the WAR in this test. Neither application *NOR JETTY* are on the (Maven provided) classpath here - only JUnit
 * is.
 *
 * @author Michael Vorburger
 */
public class MifosExecutableWARBasicTest {

    @Test
    public void testExecutableWARStartup() throws IOException, InterruptedException {
    	// maven-dependency-plugin (see pom.xml) copied it:
		final File warFile = new File("target/dependency/mifos.war");
        assertTrue(warFile.toString() + " does not exist", warFile.exists());

        String jvmArguments = "-Xmx512m -XX:MaxPermSize=256m";
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            jvmArguments = jvmArguments + " -d32";
        }

        int httpPort = 4847;
        Long timeOut = 5 * 60 * 1000L; // Give it max. 5 min to start-up


        // Could have used http://commons.apache.org/exec/ instead here, but this seemed easier:

        long startTime = System.currentTimeMillis();
        final String execWARFilePath = warFile.getAbsolutePath();
        final String port = Integer.toString(httpPort);
        final ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", execWARFilePath, port);
        processBuilder.redirectErrorStream(true);
        Process p = processBuilder.start();

        URL url = new URL("http://localhost:" + httpPort + "/mifos");

        // Now either wait until the server is up on httpPort OR timeOut is reached
        boolean keepGoing = true;
        InputStream is = p.getInputStream();
        do {
            // Pipe process output to console
            while (is.available() > 0) {
                System.out.write(is.read());
            }

            // Check if the server is up and running
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                int r = con.getResponseCode();
                if (r == 200 || r == 302) {
                    // Yeah, we're up and running! So shutdown now.
                    p.destroy();
                    keepGoing = false;
                }
            } catch (ConnectException e) {
                // Oh well; not ready yet, never mind, ignore it and move on.
            }

            // Has the server died on it's own already may be?
            try {
                int exitValue = p.exitValue();
                if (exitValue != 0) {
                    fail("Server Process died (unexpectedly), return code: " + exitValue);
                }
            } catch (IllegalThreadStateException e) {
                // Great, it's still running; so move on.
            }

            // Have we hit time out?
            if (System.currentTimeMillis() - startTime > timeOut) {
                p.destroy();
                fail("Giving up after " + timeOut + "ms; as Server Process is still not responding on " + url);
            }

            // Now have a rest before trying again
            Thread.sleep(1000);
        } while (keepGoing);
    }

}
