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

package org.mifos.config.struts.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.security.util.UserContext;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.Constants;

public class CustomFieldCategoryListTag extends BodyTagSupport {
    private String actionName;

    private String methodName;

    private String flowKey;

    public CustomFieldCategoryListTag() {
    }

    public CustomFieldCategoryListTag(String action, String method, String flow) {
        actionName = action;
        methodName = method;
        flowKey = flow;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            UserContext userContext = (UserContext) pageContext.getSession().getAttribute(Constants.USERCONTEXT);

            TagUtils.getInstance().write(pageContext, getCustomFieldCategoryList(userContext));

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

    String getCustomFieldCategoryList(UserContext userContext) throws Exception {
        XmlBuilder html = new XmlBuilder();
        html.startTag("table", "width", "95%", "border", "0", "cellspacing", "0", "cellpadding", "0");

        CustomFieldCategory[] values = CustomFieldCategory.values();
        for (int i = 0; i < values.length; i++) {
            String category = MessageLookup.getInstance().lookupLabel(values[i].name());
            html.append(getCategoryRow(values[i].name(), category));
        }

        html.endTag("table");

        return html.getOutput();
    }

    XmlBuilder getCategoryRow(String category, String categoryName) {
        String urlencodedCategoryName = replaceSpaces(categoryName);
        XmlBuilder html = new XmlBuilder();
        String url = (actionName + "?method=" + methodName + "&category=" + category + "&categoryName="
                + urlencodedCategoryName + "&currentFlowKey=" + flowKey);
        html.startTag("tr", "class", "fontnormal");
        bullet(html);
        html.startTag("td");
        html.startTag("a", "href", url);
        html.text(categoryName);
        html.endTag("a");
        html.endTag("td");
        html.endTag("tr");

        return html;
    }

    public String replaceSpaces(String name) {
        return name.trim().replaceAll(" ", "%20");
    }

    private void bullet(XmlBuilder html) {
        html.startTag("td", "width", "1%");
        html.singleTag("img", "src", "pages/framework/images/bullet_circle.gif", "width", "9", "height", "11");
        html.endTag("td");
    }

    public String getFlowKey() {
        return flowKey;
    }

    public void setFlowKey(String flowKey) {
        this.flowKey = flowKey;
    }
}
