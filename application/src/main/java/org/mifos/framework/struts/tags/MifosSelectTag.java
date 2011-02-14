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

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;
import org.apache.strutsel.taglib.html.ELSelectTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.util.helpers.LabelTagUtils;

public class MifosSelectTag extends ELSelectTag {
    private static final long serialVersionUID = 1L;

    private FieldConfig fieldConfig = FieldConfig.getInstance();

    private String keyhm = null;

    public MifosSelectTag() {
        super();
    }

    public String getKeyhm() {
        return keyhm;
    }

    public void setKeyhm(String keyhm) {
        this.keyhm = keyhm;
    }

    @Override
    public int doStartTag() throws JspException {
        if (fieldConfig.isFieldHidden(getKeyhm())) {
            return SKIP_BODY;
        } else if (!fieldConfig.isFieldHidden(getKeyhm()) && fieldConfig.isFieldManadatory(getKeyhm())) {
            TagUtils.getInstance().write(this.pageContext, renderDoStartTag());
        }
        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        if (fieldConfig.isFieldHidden(getKeyhm())) {
            return EVAL_PAGE;
        }

        String bundle = "UIResources";
        String name = org.mifos.framework.util.helpers.Constants.SELECTTAG;

        // Remove the page scope attributes we created
        pageContext.removeAttribute(Constants.SELECT_KEY);

        // Render a tag representing the end of our current form
        StringBuffer results = new StringBuffer();
        results.append("<option value= \"\">");
        String preferredUserLocale = LabelTagUtils.getInstance().getUserPreferredLocale(pageContext);
        String label = (LabelTagUtils.getInstance().getLabel(pageContext, bundle, preferredUserLocale, name, null));
        TagUtils.getInstance().write(pageContext, renderDoEndTag(label));
        return (EVAL_PAGE);
    }

    public String renderDoStartTag() {
        XmlBuilder html = new XmlBuilder();
        html.singleTag("input", "type", "hidden", "name", getKeyhm(), "value", getPropertyExpr());
        return html.toString();
    }

    public String renderDoEndTag(String label) {
        XmlBuilder html = new XmlBuilder();
        StringBuilder outputHtml = new StringBuilder();
        html.startTag("option", "value", "");
        html.text(label);
        html.endTag("option");
        outputHtml.append(html.toString());
        if (saveBody != null) {
            outputHtml.append(saveBody);
            saveBody = null;
        }
        outputHtml.append("</select>");
        return outputHtml.toString();
    }
}
