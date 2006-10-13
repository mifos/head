/**

 * MifosAlphamNumTextTag.java   version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.framework.struts.tags;


import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;
import org.apache.strutsel.taglib.html.ELTextTag;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
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
	
	private FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
	
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
	 * @exception JspException if a JSP exception has occurred
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		if(fieldConfigItf.isFieldHidden(getKeyhm()))
			return EVAL_PAGE;
		else if (!fieldConfigItf.isFieldHidden(getKeyhm()) && fieldConfigItf.isFieldManadatory(getKeyhm()) ){
			StringBuffer inputsForhidden=new StringBuffer();
			inputsForhidden.append("<input type=\"hidden\"  name=\""+getKeyhm()+"\" value=\""+getPropertyExpr()+"\"/>");
		    TagUtils.getInstance().write(this.pageContext,inputsForhidden.toString());
		}
		//get User Preferred Locale
		String preferredUserLocale=LabelTagUtils.getInstance().getUserPreferredLocale(pageContext);
		//set the javascript function to be called on blur
		this.setOnblur("return FnCheckNumChars(event,this);return FnEscape(event,this)");
		//set the javascript function to be called on keypress
		this.setOnkeypress("return FnCheckNumCharsOnPress(event,this);");
		//load the javascript files required.
		TagUtils.getInstance().write(this.pageContext,"<script src=\"pages/framework/js/func.js\"></script>");
		TagUtils.getInstance().write(this.pageContext,"<script src=\"pages/framework/js/func_"+preferredUserLocale+".js\"></script>");
		
		//return by calling the doStart() of the superclass
		return super.doStartTag();
	}


	public String getKeyhm() {
		return keyhm;
	}


	public void setKeyhm(String keyhm) {
		this.keyhm = keyhm;
	}
}
