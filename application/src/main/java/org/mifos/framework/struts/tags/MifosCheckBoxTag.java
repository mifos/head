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

package org.mifos.framework.struts.tags;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.strutsel.taglib.html.ELCheckboxTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;

public class MifosCheckBoxTag extends ELCheckboxTag {

    private FieldConfig fieldConfig = FieldConfig.getInstance();

    private String keyhm;

    public String getKeyhm() {
        return keyhm;
    }

    public void setKeyhm(String keyhm) {
        this.keyhm = keyhm;
    }

    @Override
    public int doStartTag() throws JspException {
        if (fieldConfig.isFieldHidden(getKeyhm())) {
            return EVAL_PAGE;
        } else if (!fieldConfig.isFieldHidden(getKeyhm()) && fieldConfig.isFieldManadatory(getKeyhm())) {
            TagUtils.getInstance().write(this.pageContext, renderInputsForhidden());
        }
        return super.doStartTag();
    }

    public String renderInputsForhidden() {
        XmlBuilder html = new XmlBuilder();
        html.singleTag("input", "type", "hidden", "name", getKeyhm(), "value", getPropertyExpr());
        return html.toString();
    }
}
