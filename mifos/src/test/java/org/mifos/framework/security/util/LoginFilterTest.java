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
 
package org.mifos.framework.security.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;

import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;

public class LoginFilterTest extends MifosMockStrutsTestCase {

    public LoginFilterTest() throws SystemException, ApplicationException {
        super();
    }

    private static final String TEST_ATTRIBUTE = "executedFilterChain";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * This test makes simply asserts that the method completes successfully.
     * In this case, the filter should do nothing since we have a valid
     * UserContext.
     */
    public void testNoLoginNeeded() throws Exception {
        // Create a mock request and response object
        HttpServletRequestSimulator req = this.getMockRequest();
        HttpServletResponseSimulator res = this.getMockResponse();
        
        // We'll try going to an arbitrary action
        req.setRequestURI("/multipleloansaction.do");
        
        UserContext context = TestUtils.makeUser();
        req.getSession().setAttribute(Constants.USERCONTEXT, context);
        
        assertTrue(isFilterChainExecuted(req, res));
    }
    
    public void testLoginPageItself() throws Exception {
        HttpServletRequestSimulator request = this.getMockRequest();
        HttpServletResponseSimulator response = this.getMockResponse();
        request.setRequestURI("/loginAction.do");
        request.setQueryString("method=load");
        assertTrue(isFilterChainExecuted(request, response));
	}
    
    public void testLoginActionItself() throws Exception {
        HttpServletRequestSimulator request = this.getMockRequest();
        HttpServletResponseSimulator response = this.getMockResponse();
        request.setRequestURI("/loginAction.do");
        assertTrue(isFilterChainExecuted(request, response));
	}
    
    public void testWithContextPath() throws Exception {
        HttpServletRequestSimulator request = this.getMockRequest();
        HttpServletResponseSimulator response = this.getMockResponse();
        request.setRequestURI("/foo/bar/loginAction.do");
        assertTrue(isFilterChainExecuted(request, response));
	}
    
    public void testNoSession() throws Exception {
        // Create a mock request and response object
        HttpServletRequestSimulator req = this.getMockRequest();
        HttpServletResponseSimulator res = this.getMockResponse();
          
        // We'll try going to an arbitrary action
        req.setRequestURI("/multipleloansaction.do");
        
        // NOTE: We are NOT adding a session to the request
        // object - this should force a login
        
        assertFalse(isFilterChainExecuted(req, res));
    }
    
    public void testNoUserContextLoginFilter() throws Exception {
        // Create a mock request and response object
        HttpServletRequestSimulator req = this.getMockRequest();
        HttpServletResponseSimulator res = this.getMockResponse();
          
        // We'll try going to an arbitrary action
        req.setRequestURI("/multipleloansaction.do");
        
        // NOTE: We are NOT adding the user context to the request
        // object - this should force a login
        req.getSession();
        
        assertFalse(isFilterChainExecuted(req, res));
    }
    
    /**
     * If the anonymous FilterChain is executed then the request was not
     * redirected to a login page.
     */
    private boolean isFilterChainExecuted(HttpServletRequestSimulator req, 
    	HttpServletResponseSimulator res) 
    throws Exception {
    	req.setAttribute(TEST_ATTRIBUTE, new Boolean(false));
        FilterChain chain = new FilterChain() {
            public void doFilter(ServletRequest theRequest,
                ServletResponse theResponse) throws IOException, ServletException
            {
                theRequest.setAttribute(TEST_ATTRIBUTE, new Boolean(true));
            }
        };
        
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.doFilter(req, res, chain);
        
        Object testExecute = req.getAttribute(TEST_ATTRIBUTE);
        this.assertNotNull(testExecute);
        return ((Boolean)testExecute).booleanValue();
    }
}
