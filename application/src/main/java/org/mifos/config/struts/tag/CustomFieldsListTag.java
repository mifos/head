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

package org.mifos.config.struts.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;

/**
 * @deprecated - remove - custom fields no longer supported: remove tag from web.xml etc
 */
@Deprecated
public class CustomFieldsListTag extends BodyTagSupport { // SimpleTagSupport {
    private String actionName;

    private String methodName;

    private String flowKey;

    private String categoryName;
    private String category;

    public CustomFieldsListTag() {
    }

    public CustomFieldsListTag(String action, String method, String flow, String categoryName, String category) {
        actionName = action;
        methodName = method;
        flowKey = flow;
        this.categoryName = categoryName;
        this.category = category;
    }

    private String getDefaultValue(CustomFieldDefinitionEntity customField, UserContext userContext) {
        String defaultValue = customField.getDefaultValue();
        if (customField.getFieldType().equals(CustomFieldType.DATE.getValue())
                && StringUtils.isNotBlank(defaultValue)) {
            defaultValue = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), defaultValue);
        }
        return defaultValue;
    }

    public XmlBuilder getRow(CustomFieldDefinitionEntity customField, UserContext userContext, int index) {
        XmlBuilder html = new XmlBuilder();
        String url = (actionName + "?method=" + methodName + "&customFieldIdStr=" + customField.getFieldId()
                + "&currentFlowKey=" + flowKey);

        html.startTag("tr");
        html.newline();
        html.startTag("td", "width", "11%", "class", "drawtablerow");
        html.text(Integer.toString(index));
        html.endTag("td");
        html.newline();
        html.startTag("td", "width", "22%", "class", "drawtablerow");
        String label = customField.getLookUpEntity().findLabel();
        html.text(label);
        html.endTag("td");
        html.newline();
        html.startTag("td", "width", "21%", "class", "drawtablerow");
        html.text(ApplicationContextProvider.getBean(MessageLookup.class).lookup(CustomFieldType.fromInt(customField.getFieldType())));
        html.endTag("td");
        html.newline();
        html.startTag("td", "width", "21%", "class", "drawtablerow");
        if (customField.getDefaultValue() == null || "".equals(customField.getDefaultValue())) {
            html.nonBreakingSpace();
        } else {
            html.text(getDefaultValue(customField, userContext));
        }
        html.endTag("td");
        html.newline();
        html.startTag("td", "width", "17%", "class", "drawtablerow");
        html.text(customField.getMandatoryStringValue());
        html.endTag("td");
        html.newline();
        html.endTag("tr");
        html.newline();

        return html;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USERCONTEXT);

            TagUtils.getInstance().write(pageContext, "");

        } catch (Exception e) {
            /**
             * This turns into a (rather ugly) error 500. TODO: make it more
             * reasonable.
             */
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
