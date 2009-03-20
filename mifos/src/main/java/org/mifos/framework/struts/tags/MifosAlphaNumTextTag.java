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
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.util.helpers.LabelTagUtils;

/**
 * Custom tag for input fields of type "text". The tag allows only AlphaNumeric 
 * values of the Locale
 */
public class MifosAlphaNumTextTag extends ELTextTag {
	
	/**
	 * 	Serial Version UID for Serialization 
	 */
	private static final long serialVersionUID = 1645345439876082736L;
	
	private FieldConfig fieldConfig = FieldConfig.getInstance();
	
	private String keyhm;
	
	//--------------------------------------------------------- Constructors
	
	/**
     * Construct a new instance of this tag.
     */
	public MifosAlphaNumTextTag() {
		super();
	}
	

	//--------------------------------------------------------- Public Methods
	
	
	/**
	 * Set the onBlur and onKeyPress events of the element. 
	 * @return doStart of super class
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		if(fieldConfig.isFieldHidden(getKeyhm()))
			return EVAL_PAGE;
		else if (!fieldConfig.isFieldHidden(getKeyhm()) && fieldConfig.isFieldManadatory(getKeyhm()) ){
			TagUtils.getInstance().write(this.pageContext,renderFieldHiddenMandatory());
		}
		//get User Preferred Locale
		String preferredUserLocale=LabelTagUtils.getInstance().getUserPreferredLocale(pageContext);
		//set the javascript function to be called on blur
		this.setOnblur("return FnCheckNumChars(event,this);return FnEscape(event,this)");
		//set the javascript function to be called on keypress
		this.setOnkeypress("return FnCheckNumCharsOnPress(event,this);");
		//load the javascript files required.
		TagUtils.getInstance().write(this.pageContext,renderDoStartTag(preferredUserLocale));
		
		//return by calling the doStart() of the superclass
		return super.doStartTag();
	}


	public String getKeyhm() {
		return keyhm;
	}


	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}
	
	public String renderFieldHiddenMandatory(){
		XmlBuilder html = new XmlBuilder();
		html.singleTag("input", "type","hidden","name",getKeyhm(),"value",getPropertyExpr());
		return html.toString();
	}
	
	public String renderDoStartTag(String preferredUserLocale){
    	XmlBuilder html = new XmlBuilder();
    	html.startTag("script", "src","pages/framework/js/func.js");
    	html.endTag("script");
    	html.startTag("script", "src","pages/framework/js/func_"+preferredUserLocale+".js");
    	html.endTag("script");
		return html.toString();
    }
}
