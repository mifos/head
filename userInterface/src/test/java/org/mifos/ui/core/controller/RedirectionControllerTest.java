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

package org.mifos.ui.core.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = { "unit" })
public class RedirectionControllerTest {

	public void testHandleRequest() throws ServletException, IOException {

    	String expectedPageToRedirectTo = "foopage";
        RedirectionController controller = new RedirectionController();
        controller.setViewToRedirectTo(expectedPageToRedirectTo);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ModelAndView modelAndView = controller.handleRequest(mockRequest, mockResponse);

        Assert.assertEquals(expectedPageToRedirectTo, modelAndView.getViewName());
        Assert.assertNotNull(modelAndView.getModel());
        Map<String, Object> modelMap = (Map<String, Object>) modelAndView.getModel().get("model");
        Object response = modelMap.get("response");
		Assert.assertNotNull(response);
		Assert.assertEquals(MockHttpServletResponse.class, response.getClass());
     }
}
