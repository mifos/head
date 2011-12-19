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

package org.mifos.framework.util.helpers;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.MessageResources;
import org.mifos.application.master.MessageLookup;
import org.mifos.config.Localization;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;

/**
 * This singleton is used by the MifosLabelTag to look up internationalized messages. This is legacy Struts/JSP code
 * that will eventually be replaced by SpringMVC/Freemarker code.
 */
public class LabelTagUtils {
    private static LabelTagUtils instance = new LabelTagUtils();

    private LabelTagUtils() {
    }

    public static LabelTagUtils getInstance() {
        return instance;
    }

    public String getLabel(PageContext pageContext, String bundle, String localeKey, String key, String[] args)
            throws JspException {
        return TagUtils.getInstance().message(pageContext, bundle, localeKey, key, args);
    }

    public String getLabel(PageContext pageContext, String bundle, Locale locale, String key, String[] args)
            throws JspException {
        String message = null;
        MessageResources resources = TagUtils.getInstance().retrieveMessageResources(pageContext, bundle, false);
        if (args == null) {
            message = resources.getMessage(locale, key);
        } else {
            message = resources.getMessage(locale, key, args);
        }

        if (StringUtils.isBlank(message)) {
            String labelKey = key;
            if (ConfigurationConstants.SERVICE_CHARGE.equalsIgnoreCase(key)) {
                labelKey = ConfigurationConstants.INTEREST;
            }
            if (ConfigurationConstants.CENTER.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.GROUP.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.CLIENT.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.LOAN.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.SAVINGS.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.INTEREST.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.STATE.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.POSTAL_CODE.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.ETHNICITY.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.CITIZENSHIP.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.HANDICAPPED.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.GOVERNMENT_ID.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.ADDRESS1.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.ADDRESS2.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.ADDRESS3.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.EXTERNALID.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.BULKENTRY.equalsIgnoreCase(labelKey) ||
                    ConfigurationConstants.CITY.equalsIgnoreCase(labelKey)) {
                message = MessageLookup.getInstance().lookupLabel(labelKey);
            }
        }

        if (StringUtils.isBlank(message)) {
            // FOR PersonnelUIResources.properties
            String newKey = "Personnel." + key;
            message = resources.getMessage(locale, newKey);
        }

        if (StringUtils.isBlank(message)) {
            message = "";
        }
        return MessageLookup.getInstance().replaceSubstitutions(message);
    }

    @SuppressWarnings("unchecked")
    public boolean isConfigurableMandatory(String key, PageContext pageContext) {
        // TODO get is mandatory or not from the cache.
        Map mandatoryMap = (Map) pageContext.getSession().getAttribute("ConfigurableMandatory");
        if (mandatoryMap == null) {
            return false;
        }
        return mandatoryMap.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public boolean isHidden(String key, PageContext pageContext) {
        // TODO get is hidden or not from the cache.
        Map hiddenMap = (Map) pageContext.getSession().getAttribute("Hidden");
        if (hiddenMap == null) {
            return false;
        }
        return hiddenMap.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public boolean isConfidential(String key, PageContext pageContext) {
        // TODO get is confidential or not from the cache.
        Map confidentialMap = (Map) pageContext.getSession().getAttribute("Confidential");
        if (confidentialMap == null) {
            return false;
        }
        return confidentialMap.containsKey(key);
    }

    /**
     * This helper method returns the user-preferred locale as a Java locale ID string. Note: user-preferred locales are
     * unimplemented.
     */
    public String getUserPreferredLocale(PageContext pageContext) {
        String userPreferredLocale = null;
        UserContext userContext = getUserContextFromSession(pageContext.getSession());
        if (null != userContext) {
            Locale locale = userContext.getCurrentLocale();
            if (null != locale) {
                userPreferredLocale = locale.getLanguage() + "_" + locale.getCountry();
            }

        }
        return userPreferredLocale;
    }

    public static String getUserPreferredLocaleHelper(UserContext userContext) {
        String userPreferredLocale = null;
        if (null != userContext) {
            Locale locale = userContext.getCurrentLocale();
            if (null != locale) {
                userPreferredLocale = locale.getLanguage() + "_" + locale.getCountry();
            }

        }
        return userPreferredLocale;
    }

    public Locale getUserPreferredLocaleObject(PageContext pageContext) {
        Locale locale = null;
        UserContext userContext = getUserContextFromSession(pageContext.getSession());
        if (null != userContext) {
            locale = userContext.getCurrentLocale();
        } else {
            locale = Localization.getInstance().getConfiguredLocale();
        }
        return locale;
    }

    private UserContext getUserContextFromSession(HttpSession session) {
        UserContext userContext = null;
        if (null != session) {
            userContext = (UserContext) session.getAttribute(LoginConstants.USERCONTEXT);
        }
        return userContext;
    }
}
