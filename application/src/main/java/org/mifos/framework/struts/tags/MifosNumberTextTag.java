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
import org.mifos.framework.util.helpers.LabelTagUtils;

/**
 * Custom tag for input fields of type "text". The tag allows only Numeric
 * values of the Locale
 */
public class MifosNumberTextTag extends ELTextTag {

    /**
     * Serial Version UID for Serialization
     */
    private static final long serialVersionUID = 1343241090645688732L;

    // ----------------------------------------------------- Instance Variables

    /**
     * Maximum value that can be entered in the text element
     */
    private String maxValue;

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Minimum value that can be entered in the text element
     */
    private String minValue;

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    // --------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this tag.
     */
    public MifosNumberTextTag() {
        super();

    }

    // --------------------------------------------------------- Public Methods

    /**
     * Set the onBlur and onKeyPress events of the element.
     *
     * @return int
     * @exception JspException
     *                if a JSP exception has occurred
     *
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        // get User Preferred Locale
        String preferredUserLocale = LabelTagUtils.getInstance().getUserPreferredLocale(pageContext);
        if (maxValue == null) {
            maxValue = "''";
        }
        if (minValue == null) {
            minValue = "''";
        }
        // set the javascript function to be called on blur
        this.setOnblur("FnCheckNumber(event," + minValue + "," + maxValue + ",this);");
        // set the javascript function to be called on keypress
        this.setOnkeypress("return FnCheckNumberOnPress(event);");
        // load the javascript files required.
        TagUtils.getInstance().write(this.pageContext, render(preferredUserLocale));
        return super.doStartTag();
    }

    /**
     * Release any acquired resources.
     */
    @Override
    public void release() {

        super.release();
        maxValue = null;
        minValue = null;
    }

    public String render(String preferredUserLocale) {
        XmlBuilder html = new XmlBuilder();
        html.startTag("script", "src", "pages/framework/js/func.js");
        html.endTag("script");
        html.startTag("script", "src", "pages/framework/js/func_" + preferredUserLocale + ".js");
        html.endTag("script");
        return html.toString();
    }
}
