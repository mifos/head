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

package org.mifos.framework.components.tabletag;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.struts.tags.MifosTagUtils;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class renders the link to the display name.
 */
public class Link {

    /**
     * @param displayname
     *            name to be displayed
     * @param action
     *            action to be taken
     * @param param
     *            parameters passed for that action
     * @param obj
     *            object got from the user
     * @return string string as a link.
     * @throws TableTagException
     */
    public static String getLink(PageContext pageContext, DisplayName displayname, String action, Parameters param,
            Object obj, Locale locale, String styleClass, boolean isFlowRequired) throws TableTagException {

        // Used to get the string array of display name
        String[] name = displayname.getDisplayName(pageContext, displayname.getFragment(), obj, false, locale);

        // Used to get the display name as bold string
        String bold = displayname.getBold();

        // Used to get the string array of parameters
        String[] parameters = param.getParameters(pageContext, param.getParam(), obj, locale);

        // TODO: remove fetching from Param after removal of dependency from M1
        // code.
        String flowKey = null;
        if (isFlowRequired) {
            flowKey = (String) pageContext.getRequest().getAttribute(Constants.CURRENTFLOWKEY);
            if (flowKey == null) {
                flowKey = pageContext.getRequest().getParameter(Constants.CURRENTFLOWKEY);
            }
        }
        Object randomNumber = pageContext.getSession().getAttribute(Constants.RANDOMNUM);
        return createLink(name, parameters, bold, action, styleClass, flowKey, randomNumber);

    }

    // Used to create the link
    static String createLink(String[] name, String[] parameters, String bold, String action, String styleClass,
            String flowKey, Object randomNumber) {
        StringBuilder stringbuilder = new StringBuilder();
        for (int i = 0; i < name.length; i++) {
            if (name[i] == null || name[i].trim().equals("") || name[i].trim().equals("null")) {
                return "";
            }
            if (styleClass != null && styleClass.equals("fontnormalbold") && bold.equalsIgnoreCase("true")) {
                stringbuilder.append("<span class=");
                stringbuilder.append("\"" + styleClass + "\">");
                stringbuilder.append("<a href= ");
                stringbuilder.append("\"" + action + "?" + parameters[i]);
                if (flowKey != null) {
                    stringbuilder.append("&" + Constants.CURRENTFLOWKEY + "=" + flowKey);
                }
                stringbuilder.append("&" + Constants.RANDOMNUM + "=" + randomNumber);
                stringbuilder.append("\">");
                stringbuilder.append(MifosTagUtils.xmlEscape(name[i]) + "</a></span>");
            } else if (styleClass != null && styleClass.equals("headingblue") && bold.equalsIgnoreCase("true")) {
                stringbuilder.append("<span class=");
                stringbuilder.append("\"" + styleClass + "\">");
                stringbuilder.append("<a href= ");
                stringbuilder.append("\"" + action + "?" + parameters[i] + "\"");
                if (flowKey != null) {
                    stringbuilder.append("&" + Constants.CURRENTFLOWKEY + "=" + flowKey);
                }
                stringbuilder.append("&" + Constants.RANDOMNUM + "=" + randomNumber);
                stringbuilder.append("class=\"" + styleClass + "\"");
                stringbuilder.append(">");
                stringbuilder.append(MifosTagUtils.xmlEscape(name[i]) + "</a></span>");
            } else {
                stringbuilder.append("<span>");
                stringbuilder.append("<a href= ");
                stringbuilder.append("\"" + action + "?" + parameters[i]);
                if (flowKey != null) {
                    stringbuilder.append("&" + Constants.CURRENTFLOWKEY + "=" + flowKey);
                }
                stringbuilder.append("&" + Constants.RANDOMNUM + "=" + randomNumber);
                // stringbuilder.append("class=");
                // stringbuilder.append(bold.equalsIgnoreCase("true")?"\"headingblue\">"
                // :"\"\">");
                stringbuilder.append("\">");
                stringbuilder.append(MifosTagUtils.xmlEscape(name[i]) + "</a></span>");
            }
            stringbuilder.append((i == (name.length - 1)) ? "" : ",");
        }
        return stringbuilder.toString();
    }
}
