/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.framework.struts;

import javax.servlet.ServletException;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;

/**
 * Strut's 1.2.7's ActionServlet reads a WEB-INF/web.xml itself. In the new Servlet 3.0-based workspace, this would fail
 * (because it's in a web-fragment.xml now) :
 *
 * <pre>ERROR org.apache.struts.action.ActionServlet The /WEB-INF/web.xml was not found.
 * ERROR, org.apache.struts.action.ActionServlet Unable to initialize Struts ActionServlet due to an unexpected
 * exception or error thrown, so marking the servlet as unavailable.  Most likely, this is due to an incorrect or
 * missing library dependency.
 * javax.servlet.ServletException: The /WEB-INF/web.xml was not found.
 * 	at org.apache.struts.action.ActionServlet.initServlet(ActionServlet.java:1138)</pre>
 * <p>
 * This ActionServlet subclass avoids reading WEB-INF/web.xml directly, and obtains what ActionServlet wants from
 * web.xml (just it's own URL mapping!) differently; for the short-term (Jetty v7 with Servlet 2.6 API) it's just
 * hard-coded to "*.do", and when Jetty v8 is adopted and comes with the new Servlet 3.0 API, the hard-coding can be
 * removed and some commented out code activated.)
 * <p>
 * This class is declared in the web-fragment.xml (instead of the original ActionServlet), as well as used hard-coded in
 * the MifosMockStrutsTestCase (see there for details).
 */
public class ActionServlet30 extends ActionServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void initServlet() throws ServletException {
        // DON'T super.initServlet();

        // Remember our Servlet name, copy/pasted from super:
        this.servletName = getServletConfig().getServletName();

        // For now hard-coded...
        this.servletMapping = "*.do";
        // Later (on Jetty v8 with Servlet 8.0) remove hard-coding above and use this instead:
        // ServletRegistration mappings = getServletContext().getServletRegistration(servletName);
        // this.servletMapping = mappings.getMappings().iterator().next();

        // copy/pasted from super:
        if (log.isDebugEnabled()) {
            log.debug("Mapping for servlet '" + servletName + "' = '" + servletMapping + "'");
        }

        if (servletMapping != null) {
            getServletContext().setAttribute(Globals.SERVLET_KEY, servletMapping);
        }
    }
}
