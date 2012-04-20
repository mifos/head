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
package org.mifos.ui.core.controller.util.helpers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.config.SitePreferenceType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.CookieSitePreferenceRepository;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.mobile.device.site.SitePreferenceUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.CookieGenerator;

/**
 * Helper class for managing SitePreference functionality from Spring Mobile Framework.
 * Can resolve view name (mobile or normal) depending on SitePreference cookie and
 * can set such cookie (for example after user settings changes).
 *  For setting ad-hoc site preference just add site_preference query parameter to URL with value 'normal' or 'mobile'.
 * A SitePreference cookie is set when a user logs in.  
 */
public class SitePreferenceHelper extends CookieGenerator{

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    private static final String MOBILE_SITE_PREFIX = "m_";
    private static final String DEFAULT_COOKIE_NAME = CookieSitePreferenceRepository.class.getName() + ".SITE_PREFERENCE";

    public SitePreferenceHelper() {
        super();
        setCookieName(DEFAULT_COOKIE_NAME);
    }

    public void resolveSiteType(ModelAndView modelAndView, String viewName, HttpServletRequest request) {
        SitePreference sitePreference = SitePreferenceUtils.getCurrentSitePreference(request);
        StringBuffer resultViewName = new StringBuffer(viewName);

        if (sitePreference.isMobile()) {
            resultViewName = resultViewName.insert(0, MOBILE_SITE_PREFIX);
        }

        modelAndView.setViewName(resultViewName.toString());
        
        String url = UrlHelper.constructCurrentPageUrl(request);
        request.setAttribute("currentPageUrl", url);
    }
    
    public boolean isMobile(HttpServletRequest request){
    	SitePreference sitePreference = SitePreferenceUtils.getCurrentSitePreference(request);
        
    	return sitePreference.isMobile();
    }

    public void setSitePreferenceCookie(Integer userId, HttpServletResponse response) {
        SitePreferenceType sitePreferenceType = personnelServiceFacade.retrieveSitePreference(userId);
        setSitePreferenceCookie(sitePreferenceType, response);
    }
    
    public void setSitePreferenceCookie(SitePreferenceType sitePreferenceType, HttpServletResponse response) {
        if ( sitePreferenceType == SitePreferenceType.AUTO){
            removeCookie(response);
        } else {
            addCookie(response, sitePreferenceType.name());
        }
    }

}
