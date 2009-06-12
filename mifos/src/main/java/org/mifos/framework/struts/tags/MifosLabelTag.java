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
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.util.helpers.LabelTagUtils;

/**
 * Custom tag that represents type label. The tag adds a * if the corresponding
 * field assaociated is mandatory. The tag has a currency of the Locale
 * associated with it based on our attributes.
 * 
 * NOTES: If you do not pass a "bundle" argument to this tag, then it will
 * dynamically construct the name of a resource bundle to load. In this case, if
 * you pass a "name" key in of the form FirstPart.LastPart then this tag will
 * automatically attempt to load a resource bundle with the name
 * "FirstPartUIResources". So BEWARE. It would be good to remove this behavior
 * which couples the name of a message key with the name of a resource bundle.
 * 
 */
public class MifosLabelTag extends BodyTagSupport {

    /**
     * Serial Version UID for Serialization
     */
    private static final long serialVersionUID = 1098346743243323316L;

    private FieldConfig fieldConfig = FieldConfig.getInstance();

    // ----------------------------------------------------- Instance Variables

    /**
     * The name of the key based on which the Label is to be picked from
     * Resource Bundle.
     */
    private String name;

    private String keyhm;

    private String mandatory;

    private String bundle;

    /**
     * The type of the Label is to check whether it is currency or not
     */
    private String type;

    private String isColonRequired;

    private String isManadatoryIndicationNotRequired;

    public String getIsManadatoryIndicationNotRequired() {
        return isManadatoryIndicationNotRequired;
    }

    public void setIsManadatoryIndicationNotRequired(String isManadatoryIndicationNotRequired) {
        this.isManadatoryIndicationNotRequired = isManadatoryIndicationNotRequired;
    }

    public String getIsColonRequired() {
        return isColonRequired;
    }

    public void setIsColonRequired(String isColonRequired) {
        this.isColonRequired = isColonRequired;
    }

    public String getKeyhm() {
        return keyhm;
    }

    public void setKeyhm(String keyhm) {
        this.keyhm = keyhm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    // --------------------------------------------------------- Constructors
    /**
     * Construct a new instance of this tag.
     */
    public MifosLabelTag() {
        super();
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Render the Label element
     * 
     */
    @Override
    public int doStartTag() throws JspException {

        if (fieldConfig.isFieldHidden(getKeyhm())) {
            XmlBuilder html = new XmlBuilder();
            hideLabelColumn(html);
            TagUtils.getInstance().write(pageContext, html.toString());
        } else {
            StringBuilder label = new StringBuilder();
            label.append(getLabel());
            if (getIsColonRequired() != null && getIsColonRequired().equalsIgnoreCase("yes")) {
                label.append(":");
            }
            TagUtils.getInstance().write(pageContext, label.toString());
        }
        return EVAL_PAGE;
    }

    /**
     * Release any acquired resources.
     */
    @Override
    public void release() {
        super.release();
        name = null;
        type = null;
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Create an appropriate Label element based on our parameters.
     * 
     */
    protected String getLabel() throws JspException {

        XmlBuilder html = new XmlBuilder();
        StringBuilder outputHtml = new StringBuilder();
        // check if the field is hidden
        if (LabelTagUtils.getInstance().isHidden(name, pageContext)) {
            // if the field is hidden hide the tr associated with it.
            hideLabelRow(html);
            return html.toString();
        }

        // check if the field is mandatory by default
        if ((null != mandatory && "yes".equalsIgnoreCase(mandatory)) || fieldConfig.isFieldManadatory(getKeyhm())) {
            // if it is mandatory add a *.
            if (getIsManadatoryIndicationNotRequired() != null
                    && getIsManadatoryIndicationNotRequired().equalsIgnoreCase("yes")) {
            } else {
                html.startTag("span", "class", "mandatorytext");
                html.startTag("font", "color", "#FF0000");
                html.text("*");
                html.endTag("font");
                html.endTag("span");
            }
        } else {
            // if it is not mandatory check if it is configurable mandatory
            if (LabelTagUtils.getInstance().isConfigurableMandatory(name, pageContext)) {
                // if the field is configurable mandatory add a hidden variable
                // and *.
                html.singleTag("input", "type", "hidden", "name", "hidden_" + name);
                html.startTag("span", "class", "mandatorytext");
                html.startTag("font", "color", "#FF0000");
                html.text("*");
                html.endTag("font");
                html.endTag("span");
            }
        }
        outputHtml.append(html);
        outputHtml.append(LabelTagUtils.getInstance().getLabel(pageContext, getLabelBundle(),
                LabelTagUtils.getInstance().getUserPreferredLocaleObject(pageContext), name, null));
        // check the label type.
        if (null != type && "currency".equalsIgnoreCase(type)) {
            // if label type is currency add the currency of the Locale.
            // TODO change the string locale to get the locale of the user from
            // UserContext
            // String locale =(UserContext)
            // (pageContext.getSession().getAttribute("UserContext")).getLocale();
            outputHtml.append(" (" + LabelTagUtils.getInstance().getCurrency("locale") + ") ");
        }
        return outputHtml.toString();
    }

    /**
     * This method is used to add the String which hides the tr to the
     * StringBuilder
     * 
     */
    protected void hideLabelRow(XmlBuilder html) {
        html.startTag("script", "language", "javascript");
        html.text("document.getElementById(\"" + name + "\")");
        html.text(".style.display=\"none\"}");
        html.endTag("script");
    }

    protected void hideLabelColumn(XmlBuilder html) {
        html.startTag("script", "language", "javascript");
        html.text("if(document.getElementById(\"" + getKeyhm() + "\")!=null){");
        html.text("document.getElementById(\"" + getKeyhm() + "\")");
        html.text(".style.display=\"none\";}");
        html.endTag("script");
    }

    protected String getLabelBundle() throws JspException {
        if (bundle == null) {
            String[] labelNames = name.split("\\.");
            if (labelNames.length == 2) {
                return labelNames[0] + "UIResources";
            } else {
                return "UIResources";
            }
        }
        return bundle;
    }
}
