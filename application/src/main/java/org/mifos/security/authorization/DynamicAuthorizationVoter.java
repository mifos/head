/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.security.authorization;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PreviousRequestValues;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 *
 */
public class DynamicAuthorizationVoter implements AccessDecisionVoter {

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        Object principal = authentication.getPrincipal();

        for (ConfigAttribute configAttribute : attributes) {
            if (supports(configAttribute)) {

            }
        }

        FilterInvocation filter = (FilterInvocation) object;
        String fullUrl = filter.getFullRequestUrl();
        HttpServletRequest request = filter.getHttpRequest();

        HttpSession session = request.getSession();

        PreviousRequestValues previousRequestValues = (PreviousRequestValues) session
                .getAttribute(Constants.PREVIOUS_REQUEST);
        if (null == previousRequestValues) {
            previousRequestValues = new PreviousRequestValues();
            session.setAttribute(Constants.PREVIOUS_REQUEST, previousRequestValues);
        }

        return ACCESS_GRANTED;
    }
}