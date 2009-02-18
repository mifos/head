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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class GenericController extends AbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)  {
        	Map<String, Object> model = new HashMap<String, Object>();
        	model.put("request", request);
        	Map<String, Object> status = new HashMap<String, Object>();
        	List<String> errorMessages = new ArrayList<String>();
        	status.put("errorMessages", errorMessages);
        	ModelAndView modelAndView = new ModelAndView(getPageToDisplay(request), "model", model);
        	modelAndView.addObject("status", status);
        	return modelAndView;
	}
	
	public String getPageToDisplay(HttpServletRequest request) {
		return request.getRequestURI().replace("mifos/","").replace("/", "").replace(".ftl", "");
	}
}
