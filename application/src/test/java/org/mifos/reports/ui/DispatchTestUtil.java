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

package org.mifos.reports.ui;

import static junit.framework.Assert.assertEquals;
import static junitx.framework.StringAssert.assertContains;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.dom4j.DocumentException;
import org.mifos.framework.TestUtils;

import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;
import servletunit.ServletContextSimulator;

public class DispatchTestUtil {

    public static String getSuccessfulDocument(HttpServletResponseSimulator response) throws DocumentException {
        assertEquals(200, response.getStatusCode());
        return getBody(response);
    }

    public static String getBody(HttpServletResponseSimulator response) throws DocumentException {
        String out = response.getWriterBuffer().toString();
        TestUtils.assertWellFormedDocument(out);
        return out;
    }

    public static HttpServletResponseSimulator dispatch(String path) throws Exception {
        return dispatch(path, HttpServletRequestSimulator.GET);
    }

    public static HttpServletResponseSimulator dispatchPost(String path) throws Exception {
        return dispatch(path, HttpServletRequestSimulator.POST);
    }

    private static HttpServletResponseSimulator dispatch(String path, int method) throws ServletException, IOException {
        HttpServletResponseSimulator response = new HttpServletResponseSimulator();
        HttpServletRequestSimulator request = makeRequest(path, method);
        new Dispatcher().service(request, response);
        return response;
    }

    public static HttpServletRequestSimulator makeRequest(String path, int method) {
        ServletContext context = new ServletContextSimulator();
        HttpServletRequestSimulator request = new HttpServletRequestSimulator(context);
        request.setMethod(method);
        request.setScheme("https");
        request.setServerName("test-server");
        request.setServerPort(123);
        request.setContextPath("/context");
        request.setServletPath("/developer");
        request.setPathInfo(path);
        return request;
    }

    public static void verifyNotFound(String badUrl) throws Exception {
        HttpServletResponseSimulator response = dispatch(badUrl);
        assertEquals(404, response.getStatusCode());
        String out = getBody(response);
        assertContains(badUrl + " not found", out);
    }

}
