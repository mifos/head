/**

 * MifosMasterSelectTag.java   version: 1.0

 

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
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.html.SelectTag;

/**
 * Custom tag that represents an HTML select element, associated with a
 * bean property specified by our attributes.  
 */
public class MifosMasterSelectTag extends SelectTag {

	/**
	 * 	Serial Version UID for Serialization 
	 */
	private static final long serialVersionUID = 165404539580182746L;
	
	//----------------------------------------------------- Instance Variables
	
	/**
     * The saved body content of this tag.
     */
	protected String saveBody = null;

	//--------------------------------------------------------- Constructors
	
	/**
     * Construct a new instance of this tag.
     */
	public MifosMasterSelectTag() {
		super();
	}
	
	/**
     * Construct a new instance of this tag.
     * @param pageContext 	pageContext of the Tag
     * @param property 		property of the element
     * @param size 			size of the element
     * @param multiple 		multiple attribute of the element
     * @param style			style of the element
     * @param tooltip		function to be called to enable tooltip
     */
    public MifosMasterSelectTag(PageContext pageContext,String property,String size,String multiple,
    		String style,String tooltip) {
		this.pageContext=pageContext;
		this.property=property;
		this.size=size;
		this.multiple=multiple;
		this.setStyle(style);
		this.setOnchange(tooltip);
		this.setOnmouseout(tooltip);
		this.setOnmouseover(tooltip);
	}
	
    //--------------------------------------------------------- Public Methods
    
    /**
     * Create an appropriate select start element based on our parameters.
     * @exception JspException if a JSP exception has occurred
     */
	public String getSelectString() throws JspException {
		return renderSelectStartElement();
	}
}
