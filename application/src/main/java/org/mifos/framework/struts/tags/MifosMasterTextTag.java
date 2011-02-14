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
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.html.BaseFieldTag;

/**
 * Custom tag for input fields of type "text"
 */
public class MifosMasterTextTag extends BaseFieldTag {

    /**
     * Serial Version UID for Serialization
     */
    private static final long serialVersionUID = 1564754365480837467L;

    // --------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this tag.
     */
    public MifosMasterTextTag() {
        super();
    }

    /**
     * Construct a new instance of this tag.
     *
     * @param pageContext
     *            pageContext of the Tag
     * @param property
     *            property of the element
     *
     */
    public MifosMasterTextTag(PageContext pageContext, String property, String type) {
        super();
        this.pageContext = pageContext;
        this.property = property;
        this.type = type;
        // if("hidden".equals(type))
        this.value = "";
    }

    // --------------------------------------------------------- Public Methods

    /**
     * render text element.
     */
    public String getTextString() throws JspException {
        return renderInputElement();
    }

}
