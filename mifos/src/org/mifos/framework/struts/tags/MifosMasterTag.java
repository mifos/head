/**

 * MifosMasterTag.java   version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.Constants;

/**
 * Composite custom tag containing input types text,button and select. 
 * The values entered in text box are added to the select box
 * using add button. The values in the select box are editable using edit button.
 *  
 * @author mohammedn
 */
public class MifosMasterTag extends BodyTagSupport {

	/**
	 * 	Serial Version UID for Serialization 
	 */
	private static final long serialVersionUID = 1701945584847509348L;
	
	//----------------------------------------------------- Instance Variables
	
	/**
	 * MifosText to create a text box.
	 */
	private MifosMasterTextTag text=null;
	
	/**
	 * MifosButton to create a button
	 */
	private MifosMasterButtonTag addButton=null;
	
	/**
	 * MifosButton to create a button
	 */
	private MifosMasterButtonTag editButton=null;
	
	/**
	 * MifosSelect to create a button
	 */
	private MifosMasterSelectTag select=null;
	
	/**
	 * MifosText to create a hidden variable
	 */
	private MifosMasterTextTag hiddenValue=null;
	
	/**
	 * property of the MifosText text
	 */
	private String textProperty;

	public String getTextProperty() {
		return textProperty;
	}

	public void setTextProperty(String textProperty) {
		this.textProperty = textProperty;
	}
	
	/**
	 * property of the MifosButton addButton 
	 */
	private String addButtonProperty=null;

	public String getAddButtonProperty() {
		return addButtonProperty;
	}

	public void setAddButtonProperty(String addButtonProperty) {
		this.addButtonProperty = addButtonProperty;
	}
	
	/**
	 * value of the MifosButton addButton 
	 */
	private String addButtonValue=null;

	public String getAddButtonValue() {
		return addButtonValue;
	}

	public void setAddButtonValue(String addButtonValue) {
		this.addButtonValue = addButtonValue;
	}

	/**
	 * onClick value of the MifosButton addButton 
	 */
	private String addOnClick=null;
	
	public String getAddOnClick() {
		return addOnClick;
	}

	public void setAddOnClick(String addOnClick) {
		this.addOnClick = addOnClick;
	}
	
	/**
	 * property of the MifosButton editButton 
	 */
	private String editButtonProperty=null;

	public String getEditButtonProperty() {
		return editButtonProperty;
	}

	public void setEditButtonProperty(String editButtonProperty) {
		this.editButtonProperty = editButtonProperty;
	}
	/**
	 * value of the MifosButton editButton 
	 */
	private String editButtonValue=null;

	public String getEditButtonValue() {
		return editButtonValue;
	}

	public void setEditButtonValue(String editButtonValue) {
		this.editButtonValue = editButtonValue;
	}

	/**
	 * onClick value of the MifosButton editButton 
	 */
	private String editOnClick=null;

	public String getEditOnClick() {
		return editOnClick;
	}

	public void setEditOnClick(String editOnClick) {
		this.editOnClick = editOnClick;
	}

	/**
	 * property of the MifosSelect select 
	 */
	private String selectProperty=null;

	public String getSelectProperty() {
		return selectProperty;
	}

	public void setSelectProperty(String selectProperty) {
		this.selectProperty = selectProperty;
	}

	/**
	 * size of the MifosSelect select 
	 */
	private String selectSize=null;

	public String getSelectSize() {
		return selectSize;
	}
	
	public void setSelectSize(String selectSize) {
		this.selectSize = selectSize;
	}

	/**
	 * multipleselection for the MifosSelect select 
	 */
	private String selectMultiple=null;

	public String getSelectMultiple() {
		return selectMultiple;
	}

	public void setSelectMultiple(String selectMultiple) {
		this.selectMultiple = selectMultiple;
	}

	/**
	 * style for the MifosSelect select 
	 */	
	private String selectStyle=null;

	public String getSelectStyle() {
		return selectStyle;
	}

	public void setSelectStyle(String selectStyle) {
		this.selectStyle = selectStyle;
	}

	/**
	 * Source path of the script files 
	 */	
	private String scriptSrc=null;

	public String getScriptSrc() {
		return scriptSrc;
	}

	public void setScriptSrc(String scriptSrc) {
		this.scriptSrc = scriptSrc;
	}
	
	//--------------------------------------------------------- Constructors
	
	/**
     * Construct a new instance of this tag.
     */
	public MifosMasterTag() {
		super();
	}

	//--------------------------------------------------------- Public Methods
	
	/**
     * Render the beginning of the master tag.
     * @exception JspException if a JSP exception has occurred
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		//get the source files of javascript
		TagUtils.getInstance().write(this.pageContext, getJavaScriptSource());
		TagUtils.getInstance().write(this.pageContext, "<table><tr><td>");
		TagUtils.getInstance().write(this.pageContext, "Enter Value:");
		TagUtils.getInstance().write(this.pageContext, "</td><td>");
		//create instance of MifosText
		text=new MifosMasterTextTag(this.pageContext,textProperty,"text");
		TagUtils.getInstance().write(this.pageContext, text.getTextString());
		TagUtils.getInstance().write(this.pageContext, "</td><td>");
		//create instance of MifosButton
		addButton=new MifosMasterButtonTag(this.pageContext,addButtonProperty,addButtonValue,addOnClick);
		TagUtils.getInstance().write(this.pageContext, addButton.getButtonString());
		TagUtils.getInstance().write(this.pageContext, "</td></tr><tr><td colspan=2>");
		//create instance of MifosSelect
		select=new MifosMasterSelectTag(this.pageContext,selectProperty,selectSize,selectMultiple,
				selectStyle,"fnSelectToolTip(event)");
		TagUtils.getInstance().write(this.pageContext, select.getSelectString());
		//Store select tag as a page attribute
        pageContext.setAttribute(Constants.SELECT_KEY, select);
		
        return super.doStartTag();
	}
	
	/**
     * Save any body content of this tag, which will generally be the
     * option(s) representing the values displayed to the user.
     * @exception JspException if a JSP exception has occurred
	 * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
	 */
	public int doAfterBody() throws JspException {
		 if (bodyContent != null) {
            String value = bodyContent.getString();
            if (value == null) {
                value = "";
            }
            select.saveBody = value.trim();
        }
        return (SKIP_BODY);
	}
	
	/**
     * Render the end of this tag.
     * @exception JspException if a JSP exception has occurred
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		 // Remove the page scope attributes we created
        pageContext.removeAttribute(Constants.SELECT_KEY);

        // Render a tag representing the end of our current form
        StringBuffer results = new StringBuffer();
        if (select.saveBody != null) {
            results.append(select.saveBody);
            select.saveBody = null;
        }
        results.append("</select>");

        TagUtils.getInstance().write(pageContext, results.toString());
        //get the span to diaplay tooltip
        TagUtils.getInstance().write(pageContext, getSpan());
        TagUtils.getInstance().write(this.pageContext, "</td><td>");
        //instance of MifosButton
        editButton=new MifosMasterButtonTag(this.pageContext,editButtonProperty,
        		editButtonValue,editOnClick);
		TagUtils.getInstance().write(this.pageContext, editButton.getButtonString());
		TagUtils.getInstance().write(this.pageContext, "</td></tr></table>");
		//Instance of MifosText
		hiddenValue=new MifosMasterTextTag(this.pageContext,"hiddenValue","hidden");
		TagUtils.getInstance().write(this.pageContext, hiddenValue.getTextString());
		
        return (EVAL_PAGE);
	}

	/**
     * Release any acquired resources.
     */
    public void release() {
        super.release();
        textProperty = null;
        addButtonProperty = null;
        addButtonValue = null;
        addOnClick = null;
        editButtonProperty = null;
        editButtonValue = null;
        editOnClick = null;
        selectMultiple=null;
        selectProperty=null;
        selectSize=null;
        scriptSrc=null;
    }

	//------------------------------------------------------ Protected Methods
	
    /**
     * render the source of the script file
     * 
     */
	protected String getJavaScriptSource() {
		StringBuffer result=new StringBuffer();
		result.append("<script src=\"");
		if(getScriptSrc()!=null)
			result.append(getScriptSrc());
		result.append("\"></script>");
		return result.toString();
	}
	
	/**
	 * Render the span to display tooltip
	 * 
	 */
	protected String getSpan() {
		StringBuffer result=new StringBuffer();
		result.append("<span id=\"s1\"");
		result.append("style=\"BORDER-RIGHT: black 1px solid; PADDING-RIGHT: 3px;");
		result.append("BORDER-TOP: black 1px solid;PADDING-LEFT: 3px; PADDING-BOTTOM: 3px; ");
		result.append("BORDER-LEFT: black 1px solid; PADDING-TOP: 3px;");
		result.append("BORDER-BOTTOM: black 1px solid;POSITION: absolute; DISPLAY: none\" >");
		result.append("</span>");
		return result.toString();
	}

}
