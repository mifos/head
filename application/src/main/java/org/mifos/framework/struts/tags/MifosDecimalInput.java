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
import org.apache.strutsel.taglib.html.ELTextTag;
import org.mifos.framework.components.configuration.business.Configuration;

/**
 * MifosDecimalInput is custom input tag which takes only the decimal character
 * based on the user locale .This tag uses two javascript files in which one is
 * conversion file and another is userpreferredlocale file
 */
public class MifosDecimalInput extends ELTextTag {

    private String decimalFmt;
    private String max;
    private String min;

    public MifosDecimalInput() {
        super();
    }

    /**
     * Function Get the decinal format of the tag
     * 
     * @return decimalFmt the decimal format of the tag
     */
    public String getDecimalFmt() {
        return decimalFmt;
    }

    /**
     * Function Set the decimal format for the tag
     * 
     * @param decimalFmt
     *            the decimal format of the tag
     */
    public void setDecimalFmt(String decimalFmt) {
        this.decimalFmt = decimalFmt;
    }

    /**
     * Function Get the maximam value tag can take
     * 
     * @return max maximam value tag can take
     */
    public String getMax() {
        return max;
    }

    /**
     * Function Set the maximum value tag can take
     * 
     * @param max
     *            maximam value tag can take
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * Function Get the minimum value tag can take
     * 
     * @return min minimum value tag can take
     */
    public String getMin() {
        return min;
    }

    /**
     * Function Set the min value tag can take
     * 
     * @param min
     *            minimum value tag can take
     */
    public void setMin(String min) {
        this.min = min;
    }

    /**
     * This function renders the tag
     * 
     * @return int
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        // String preferredUserLocale =
        // LabelTagUtils.getInstance().getUserPreferredLocale(pageContext);
        if (decimalFmt == null || decimalFmt == "")
            decimalFmt = "7."
                    + Configuration.getInstance().getSystemConfig().getCurrency().getDefaultDigitsAfterDecimal();
        this.setOnblur("if( false == doValidation(this," + getMin() + "," + getMax() + "," + getDecimalFmt()
                + ")) this.focus();");
        this.setOnkeyup("return chekDecimal(" + getDecimalFmt() + ",this, event); ");
        this.setOnkeydown("saveText(this);this.focus();");
        this.setOnkeypress("return keyPress(" + getDecimalFmt() + ",this,event);");

        // TODO: This is bad - adding these script tags to each & every
        // decimalinput tag adds considerable overhead
        // always include conversion.js file and con_preferredlocale.js file
        // TagUtils.getInstance().write(this.pageContext,"<script src=\"pages/framework/js/conversion.js\"></script>");
        // TagUtils.getInstance().write(this.pageContext,"<script src=\"pages/framework/js/con_en.js\"></script>");
        // TagUtils.getInstance().write(this.pageContext,"<script src=\"pages/framework/js/con_"+preferredUserLocale+".js\"></script>");
        TagUtils.getInstance().write(this.pageContext, "<SCRIPT >makeRegEx2(" + decimalFmt + ")</SCRIPT>");

        return super.doStartTag();
    }
}
