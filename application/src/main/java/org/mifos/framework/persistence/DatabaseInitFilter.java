/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.persistence;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.struts.tags.XmlBuilder;

public class DatabaseInitFilter implements Filter {

    private static boolean databaseVerified = false;
    private static int databaseVersion = -1;

    public DatabaseInitFilter() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (!databaseVerified) {
            showError(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void showError(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("text/html");
        httpResponse.setStatus(500);
        PrintWriter out = httpResponse.getWriter();

        int version = databaseVersion;

        printErrorPage(out, version);
    }

    void printErrorPage(PrintWriter out, int version) {
        XmlBuilder xml = new XmlBuilder();
        xml.startTag("html");
        xml.startTag("head");
        xml.startTag("title");
        xml.text("Mifos Database Error");
        xml.text("\n");
        xml.endTag("title");
        xml.text("\n");
        xml.endTag("head");
        xml.text("\n");

        xml.startTag("body");
        xml.startTag("h2");
        xml.text("Mifos Database Error");
        xml.endTag("h2");
        xml.text("\n");

        xml.startTag("p");
        xml.text("A database error occurred. ");
        xml.text("Correct the error and restart the application. ");

        xml.text("Details:");

        xml.endTag("p");
        xml.text("\n");

        ApplicationInitializer.printDatabaseError(xml, version);

        xml.endTag("body");
        xml.text("\n");
        xml.endTag("html");
        out.println(xml.getOutput());
    }

    public void init(FilterConfig filterConfig) {
        try {
            DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
            if (persistence.isVersioned()) {
                databaseVersion = persistence.read();
                databaseVerified = (databaseVersion == DatabaseVersionPersistence.APPLICATION_VERSION);
            } else {
                databaseVerified = false;
            }
        } catch (Exception e) {
            filterConfig.getServletContext().log("Failed to check database version", e);
        }
    }

    public void destroy() {
    }

}
