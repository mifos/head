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
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.html.ButtonTag;

/**
 * Renders an HTML BUTTON tag within the Struts framework.
 */
public class MifosMasterButtonTag extends ButtonTag {

    /**
     * Serial Version UID for Serialization
     */
    private static final long serialVersionUID = 1098546454658944584L;

    // ----------------------------------------------------- Constructors

    /**
     * Construct a new instance of this tag.
     */
    public MifosMasterButtonTag() {
        super();
    }

    /**
     * Construct a new instance of this tag.
     *
     * @param pageContext
     *            pageContext of the Tag
     * @param property
     *            property of the element
     * @param value
     *            value of the element
     * @param onClick
     *            onClick attribute of the element
     */
    public MifosMasterButtonTag(PageContext pageContext, String property, String value, String onClick) {
        this.pageContext = pageContext;
        this.property = property;
        this.value = value;
        this.setOnclick(onClick);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the Button element.
     *
     * @exception JspException
     *                if a JSP exception has occurred
     */
    public String getButtonString() throws JspException {
        StringBuffer results = new StringBuffer();
        results.append(getElementOpen());
        prepareAttribute(results, "name", prepareName());
        prepareButtonAttributes(results);
        results.append(prepareEventHandlers());
        results.append(prepareStyles());
        prepareOtherAttributes(results);
        results.append(getElementClose());
        return results.toString();
    }

    // --------------------------------------------------------- Protected
    // Methods

    /**
     * Render the opening element.
     *
     * @return The opening part of the element.
     */
    @Override
    protected String getElementOpen() {
        return "<input type=\"button\"";
    }

    /**
     * Prepare the name element
     *
     * @return The element name.
     */
    @Override
    protected String prepareName() throws JspException {

        if (property == null) {
            return null;
        }

        // * @since Struts 1.1
        if (indexed) {
            StringBuffer results = new StringBuffer();
            results.append(property);
            prepareIndex(results, null);
            return results.toString();
        }

        return property;

    }

    /**
     * Prepares an attribute if the value is not null, appending it to the the
     * given StringBuffer.
     *
     * @param handlers
     *            The StringBuffer that output will be appended to.
     */
    @Override
    protected void prepareAttribute(StringBuffer handlers, String name, Object value) {
        if (value != null) {
            handlers.append(" ");
            handlers.append(name);
            handlers.append("=\"");
            handlers.append(value);
            handlers.append("\"");
        }
    }

    /**
     * Render the button attributes
     *
     * @param results
     *            The StringBuffer that output will be appended to.
     */
    @Override
    protected void prepareButtonAttributes(StringBuffer results) throws JspException {
        prepareAttribute(results, "accesskey", getAccesskey());
        prepareAttribute(results, "tabindex", getTabindex());
        prepareValue(results);
    }

    /**
     * 'Hook' to enable tags to be extended and additional attributes added.
     *
     * @param handlers
     *            The StringBuffer that output will be appended to.
     */
    @Override
    protected void prepareOtherAttributes(StringBuffer handlers) {
    }

    /**
     * Render the value element
     *
     * @param results
     *            The StringBuffer that output will be appended to.
     */
    @Override
    protected void prepareValue(StringBuffer results) {

        // Acquire the label value we will be generating
        String label = value;
        if ((label == null) && (text != null))
            label = text;
        if ((label == null) || (label.length() < 1))
            label = getDefaultValue();

        prepareAttribute(results, "value", label);

    }

    /**
     * Return the default value.
     *
     * @return The default value if none supplied.
     */
    @Override
    protected String getDefaultValue() {
        return "Submit";
    }
}
