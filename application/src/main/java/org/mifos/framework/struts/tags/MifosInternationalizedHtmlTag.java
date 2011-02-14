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

package org.mifos.framework.struts.tags;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;
import org.apache.strutsel.taglib.html.ELHtmlTag;
import org.mifos.config.ConfigLocale;

/**
 * Renders an HTML <html> element with appropriate language attributes
 * from the Mifos applicationConfiguration.custom.properties.
 *
 * Based on the Struts version of this tag.
 *
 */
public class MifosInternationalizedHtmlTag extends ELHtmlTag {

    private static final String ARABIC_LANGUAGE = "ar";
    private static final String RTL = "rtl";
    private static final String LTR = "ltr";

    /**
     * Renders an &lt;html&gt; element with appropriate language attributes.
     * @since Struts 1.2
     */
    @Override
    protected String renderHtmlStartElement() {
        StringBuffer sb = new StringBuffer("<html");

        String languageCode = null;
        String countryCode = "";
        String direction = null;

        ConfigLocale configLocale = new ConfigLocale();
        countryCode = configLocale.getCountryCode();
        languageCode = configLocale.getLanguageCode();
        direction = configLocale.getDirection();
        setLocaleIntoHttpSession(languageCode, countryCode);

        boolean validLanguage = ((languageCode != null) && (languageCode.length() > 0));
        boolean validCountry = countryCode.length() > 0;

        if (this.xhtml) {
            this.pageContext.setAttribute(
                Globals.XHTML_KEY,
                "true",
                PageContext.PAGE_SCOPE);

            sb.append(" xmlns=\"http://www.w3.org/1999/xhtml\"");
        }

        if ((this.lang || this.locale || this.xhtml) && validLanguage) {
            sb.append(" lang=\"");
            appendLangIfCountryIsValid(sb, languageCode, countryCode, validCountry);
        }

        if (this.xhtml && validLanguage) {
            sb.append(" xml:lang=\"");
            appendLangIfCountryIsValid(sb, languageCode, countryCode, validCountry);
        }

        appendLang(sb, languageCode);
        setDirection(sb, languageCode, direction);
        sb.append(">");
        return sb.toString();
    }

    private void appendLangIfCountryIsValid(StringBuffer sb, String languageCode, String countryCode,
            boolean validCountry) {
        sb.append(languageCode);
        if (validCountry) {
            sb.append("-");
            sb.append(countryCode);
        }
        sb.append("\"");
    }

    private void setDirection(StringBuffer sb, String languageCode, String direction) {
        if (!ConfigLocale.DEFAULT_DIRECTION.equals(direction)) {
            appendDirection(sb, direction);
        } else {
            autoDetectAndSetDirection(sb, languageCode);
        }
    }

    private void autoDetectAndSetDirection(StringBuffer sb, String languageCode) {
        String calculatedDirection = getDirection(languageCode);
        if (RTL.equals(calculatedDirection)) {
            appendDirection(sb, RTL);
        }
    }

    private void appendDirection(StringBuffer sb, String directionToAppend) {
        sb.append(" dir=\"");
        sb.append(directionToAppend);
        sb.append("\"");
    }

    private void appendLang(StringBuffer sb, String languageCode) {
        sb.append(" lang=\"");
        sb.append(languageCode);
        sb.append("\"");
    }

    private String getDirection(String language) {
        String result = LTR;
        if (ARABIC_LANGUAGE.equalsIgnoreCase(language)) {
            result = RTL;
        }
        return result;
    }

    private void setLocaleIntoHttpSession(String languageCode, String countryCode) {
        Locale userLocale = new Locale(languageCode, countryCode);
        HttpSession session = ((HttpServletRequest) this.pageContext.getRequest()).getSession();
        session.setAttribute(Globals.LOCALE_KEY, userLocale);
    }


}
