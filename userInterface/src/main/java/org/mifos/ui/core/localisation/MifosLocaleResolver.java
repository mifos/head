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

package org.mifos.ui.core.localisation;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

public class MifosLocaleResolver extends AbstractLocaleResolver {

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    @Override
    public Locale resolveLocale(@SuppressWarnings("unused") HttpServletRequest request) {
        setDefaultLocale(personnelServiceFacade.getUserPreferredLocale());
        return personnelServiceFacade.getUserPreferredLocale();
    }

    @Override
    public void setLocale(@SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") HttpServletResponse response, Locale locale) {
        super.setDefaultLocale(locale);
    }

}
