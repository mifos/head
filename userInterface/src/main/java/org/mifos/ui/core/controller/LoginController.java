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

package org.mifos.ui.core.controller;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.SystemInformationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/login")
@SuppressWarnings("PMD")
public class LoginController {

    protected LoginController() {
        // default contructor for spring autowiring
    }
    
    @Autowired
	private SystemInformationServiceFacade systemInformationServiceFacade;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView showForm(HttpServletRequest request,
			SitePreference sitePreference) {
		ModelAndView modelAndView = new ModelAndView("login");
		Device currentDevice = DeviceUtils.getCurrentDevice(request);
        ServletContext context = request.getSession().getServletContext();
        Locale locale = request.getLocale();
        
		String serverInfo = this.systemInformationServiceFacade.getServerInformation(context, locale);
		Pattern server_version = Pattern.compile("^jetty\\/7\\.3\\..*");
		Matcher matcher = server_version.matcher(serverInfo);
		Boolean isJetty = false;
		if (matcher.matches()) {
			isJetty = true;
		}
		modelAndView.addObject("isJetty" ,isJetty);
		
		if (currentDevice.isMobile()) {
			modelAndView = new ModelAndView("m_login");
		}


        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.HEAD)
    public ModelAndView showFormHead(HttpServletRequest request, SitePreference sitePreference) {
        return showForm(request, sitePreference);
    }
}
