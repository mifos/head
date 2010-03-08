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

package org.mifos.framework.components.mifosmenu;

import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.BaseHandlerTag;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.LabelTagUtils;

/**
 * It accepts top menu tab name and accordingly generates and displays left
 * menu.
 */
public class MenuTag extends BaseHandlerTag {
    private String topMenuTab;
    private Menu leftMenu = null;

    /**
     * Method to find left menu respective to given topMenuTab
     *
     * @return integer to indicate what to do next after executing the do
     *         function.
     */
    @Override
    public int doStartTag() throws JspException {
        StringBuilder output = new StringBuilder();
        // Locale locale =
        // TagUtils.getInstance().getUserLocale(pageContext,getLocale());
        Locale locale = LabelTagUtils.getInstance().getUserPreferredLocaleObject(pageContext);
        MenuRepository menuRepository = MenuRepository.getInstance();
        leftMenu = menuRepository.getMenuForTabAndLocale(topMenuTab, locale);
        try {
            if (leftMenu == null) {
                MenuBuilder.buildMenuForLocale(pageContext);
                leftMenu = menuRepository.getMenuForTabAndLocale(topMenuTab, locale);
                if (leftMenu == null) // TODO: to be modified for exception from
                                      // resource bundle
                    ;// System.out.println("error: menu could not built for locale");
            }
        } catch (SystemException mpe) {
            throw new JspException(mpe);
        }

        output.append("<td class=\"leftpanelinks\" colspan=\"2\">");
        MenuGroup menuGroups[] = leftMenu.getMenuGroups();
        for (int i = 0; i < menuGroups.length; i++) {
            prepareMenu(output, menuGroups[i]);
        }
        output.append("</td>");
        TagUtils.getInstance().write(pageContext, output.toString());
        return SKIP_BODY;
    }

    /**
     * Method to get the value of topMenuTab member variable
     *
     * @return value of topMenuTab
     */
    public String getTopMenuTab() {
        return topMenuTab;
    }

    /**
     * Method to set the value of topMenuTab member variable
     *
     * @param topMenuTab
     *            is the value to be set
     */
    public void setTopMenuTab(String topMenuTab) {
        this.topMenuTab = topMenuTab;
    }

    /**
     * Method to prepare html to display menu items
     *
     * @param output
     *            is the string to which generated html is to be appended
     * @param menuGroup
     *            whose menuitem details are to be appended as html
     */
    private void prepareMenu(StringBuilder output, MenuGroup menuGroup) {
        output.append("<span class=\"fontnormalbold\" >");
        output.append(menuGroup.getDisplayName()[0].toString() + "</span><br>");
        MenuItem menuItems[] = menuGroup.getMenuItems();
        for (int i = 0; i < menuItems.length; i++) {
            output.append(getLink(menuItems[i]));
        }
        output.append("<br>");
    }

    private String getLink(MenuItem menuItem) {
        String linkText = menuItem.getDisplayName()[0];
        String linkId = "menu.link." + menuItem.getLinkId()/*replace(' ', '.').toLowerCase()*/;
        String linkHref = menuItem.getLinkValue();
        return "<a href=\"" + linkHref + "\" id=\"" + linkId + "\">" + linkText + "</a><br>\n";
    }

}