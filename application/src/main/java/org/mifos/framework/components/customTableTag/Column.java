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

package org.mifos.framework.components.customTableTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.master.MessageLookup;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.LabelTagUtils;

public class Column {

    private String label = null;

    private String value = null;

    private String valueType = null;

    private String columnType = null;

    private ColumnDetails columnDetails = null;

    private LinkDetails linkDetails = null;

    private MifosConfiguration labelConfig = MifosConfiguration.getInstance();

    public void setLinkDetails(LinkDetails linkDetails) {
        this.linkDetails = linkDetails;
    }

    public LinkDetails getLinkDetails() {
        return linkDetails;
    }

    public void setColumnDetials(ColumnDetails columnDetails) {
        this.columnDetails = columnDetails;
    }

    public ColumnDetails getColumnDetails() {
        return columnDetails;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getValueType() {
        return valueType;
    }

    public String getColumnType() {
        return columnType;
    }

    public void getColumnHeader(StringBuilder tableInfo, PageContext pageContext, String bundle) throws JspException {
        tableInfo.append("<td ");
        tableInfo.append(" width=\"" + getColumnDetails().getColWidth() + "%\"");
        tableInfo.append(" align=\"" + getColumnDetails().getAlign() + "\" ");
        tableInfo.append(">");
        if (getLabel().replaceAll("", "").equals("") || getLabel() == null)
            tableInfo.append("&nbsp;");
        else
            tableInfo.append("<b>" + getLabelText(pageContext, getLabel(), bundle) + "</b>");
        tableInfo.append("</td>");
    }

    public void generateTableColumn(StringBuilder tableInfo, Object obj, Locale locale, Locale prefferedLocale,
            Locale mfiLocale) throws TableTagParseException {
        tableInfo.append("<td class=\"" + getColumnDetails().getRowStyle() + "\"  ");

        tableInfo.append(" align=\"" + getColumnDetails().getAlign() + "\" ");
        tableInfo.append("> ");

        if (getValueType().equalsIgnoreCase(TableTagConstants.METHOD)) {
            if (getColumnType().equalsIgnoreCase(TableTagConstants.TEXT)) {
                getTableColumn(tableInfo, obj, locale, prefferedLocale, mfiLocale);
            } else {
                // Generate Link On Column
                getTableColumnWithLink(tableInfo, obj, locale, prefferedLocale, mfiLocale);
            }
        } else {
            if (getColumnType().equalsIgnoreCase(TableTagConstants.TEXT)) {
                // ColumnType should be link
                throw new TableTagParseException(getColumnType());
            }

            getLinkWithoutName(tableInfo, obj);
            tableInfo.append("");
        }

        tableInfo.append("</td>");

    }

    public void getTableColumn(StringBuilder tableInfo, Object obj, Locale locale, Locale prefferedLocale,
            Locale mfiLocale) throws TableTagParseException {
        Method[] methods = obj.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase("get".concat(getValue()))) {
                try {
                    tableInfo.append(methods[i].invoke(obj, new Object[] {}));
                } catch (IllegalAccessException e) {
                    throw new TableTagParseException(e);
                } catch (InvocationTargetException ex) {
                    throw new TableTagParseException(ex);
                }
            }
            if (methods[i].getName().equalsIgnoreCase("setLocale") && locale != null) {
                try {
                    Object[] argumentLocale = new Object[] { locale };
                    methods[i].invoke(obj, argumentLocale);
                } catch (IllegalAccessException e) {
                    throw new TableTagParseException(e);
                } catch (InvocationTargetException ex) {
                    throw new TableTagParseException(ex);
                }
            }
            if (methods[i].getName().equalsIgnoreCase("setMfiLocale") && mfiLocale != null) {
                try {
                    Object[] argumentLocale = new Object[] { mfiLocale };
                    methods[i].invoke(obj, argumentLocale);
                } catch (IllegalAccessException e) {
                    throw new TableTagParseException(e);
                } catch (InvocationTargetException ex) {
                    throw new TableTagParseException(ex);
                }
            }
        }
    }

    public void getTableColumnWithLink(StringBuilder tableInfo, Object obj, Locale locale, Locale prefferedLocale,
            Locale mfiLocale) throws TableTagParseException {
        tableInfo.append("<a ");
        linkDetails.generateLink(tableInfo, obj);
        tableInfo.append(" >");
        getTableColumn(tableInfo, obj, locale, prefferedLocale, mfiLocale);
        tableInfo.append("</a>");
    }

    public void getLinkWithoutName(StringBuilder tableInfo, Object obj) throws TableTagParseException {
        tableInfo.append("<a ");
        linkDetails.generateLink(tableInfo, obj);
        tableInfo.append(" >");
        tableInfo.append(getValue());
        tableInfo.append("</a>");
    }

    private String getLabelText(PageContext pageContext, String key, String bundle) throws JspException {

        UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USER_CONTEXT_KEY);
        LabelTagUtils labelTagUtils = LabelTagUtils.getInstance();
        String labelText = null;
        if (labelText == null)
            try {
                labelText = labelTagUtils.getLabel(pageContext, bundle, userContext.getPreferredLocale(), key, null);
            } catch (Exception e) {
            }
        if (labelText == null)
            labelText = MessageLookup.getInstance().lookup(key);
        if (labelText == null)
            try {
                char[] charArray = bundle.toCharArray();
                charArray[0] = Character.toUpperCase(charArray[0]);
                bundle = new String(charArray);
                labelText = labelTagUtils.getLabel(pageContext, bundle, userContext.getPreferredLocale(), key, null);

            } catch (Exception e) {
                labelText = key;

            }
        return labelText;
    }

}
