/**

 * Column.java    version: 1.0

 

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

package org.mifos.framework.components.tabletag;

import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders the columns.
 */

public class Column {

	/**Used to set the value of label */
	private String label;

	/**Used to set the value of labeltype */
	private String labeltype;

	/**Used to set the value of boldlabel */
	private String boldlabel;

	/**Used to set the value of type */
	private String type;

	/**Used to set the value of action */
	private String action;

	/**Used to set the value of image */
	private String image;

	/**Used to set the value of displayname */
	private DisplayName displayname;

	/**Used to set the value of parameters */
	private Parameters parameters;
	
	private String isLinkOptional;
	
	private String checkLinkOptionalRequired;
	
	private String styleClass;

	
	
	/**
	 * @return Returns the styleClass.
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param styleClass The styleClass to set.
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/**
	 * @return Returns the checkLinkOptionalRequired.
	 */
	public String getCheckLinkOptionalRequired() {
		return checkLinkOptionalRequired;
	}

	/**
	 * @param checkLinkOptionalRequired The checkLinkOptionalRequired to set.
	 */
	public void setCheckLinkOptionalRequired(String checkLinkOptionalRequired) {
		this.checkLinkOptionalRequired = checkLinkOptionalRequired;
	}

	/**
	 * @return Returns the isLinkOptional.
	 */
	public String getIsLinkOptional() {
		return isLinkOptional;
	}

	/**
	 * @param isLinkOptional The isLinkOptional to set.
	 */
	public void setIsLinkOptional(String isLinkOptional) {
		this.isLinkOptional = isLinkOptional;
	}

	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return Returns the boldlabel.
	 */
	public String getBoldlabel() {
		return boldlabel;
	}

	/**
	 * @param boldlabel
	 *            The boldlabel to set.
	 */
	public void setBoldlabel(String boldlabel) {
		this.boldlabel = boldlabel;
	}

	/**
	 * @return Returns the displayname.
	 */
	public DisplayName getDisplayname() {
		return displayname;
	}

	/**
	 * @param displayname
	 *            The displayname to set.
	 */
	public void setDisplayname(DisplayName displayname) {
		this.displayname = displayname;
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return Returns the labeltype.
	 */
	public String getLabeltype() {
		return labeltype;
	}

	/**
	 * @param labeltype
	 *            The labeltype to set.
	 */
	public void setLabeltype(String labeltype) {
		this.labeltype = labeltype;
	}

	/**
	 * @return Returns the parameters.
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            The parameters to set.
	 */
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return Returns the image.
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            The image to set.
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * Default constructor
	 */
	public Column() {
		super();
	}
	
	/**
	 * Function to get the column
	 * @param obj 	Object from the user.
	 * @return 		Column string.
	 * @throws TableTagException 
	 */
	public String getColumn(PageContext pageContext ,Object obj,Locale locale, boolean isFlowRequired) throws TableTagException {
		//Used to get the value of the link and text
		String displayString = getColumnValue(pageContext,obj,locale, isFlowRequired);
		 
		//To check whether display string is empty or not.
		if (displayString.trim().equals("")) {
			return "";
		}
		
		StringBuilder stringColumn = new StringBuilder();
		
		// Used to get the value of label according to labeltype using helper method
		getLabel(pageContext,stringColumn,obj,locale);
		
		stringColumn.append(displayString);
		
		return stringColumn.toString();
	}
	/**
	 * Function to get label according to label type
	 * @param stringColumn
	 * @param obj
	 * @throws TableTagException
	 */
	private void getLabel(PageContext pageContext ,StringBuilder stringColumn,Object obj,Locale locale) throws TableTagException{
		Object value = TableTagUtils.getInstance().helper(pageContext,label, labeltype, obj,locale);
		stringColumn.append("<span class=\"")
			.append(boldlabel.equalsIgnoreCase("true")?"fontnormalbold":"fontnormal")
			.append("\">")
			.append(value);	
		if (! "/".equalsIgnoreCase((String)value) && ! ("").equals(((String)value).trim())) {
			stringColumn.append(": ");
		} 
		stringColumn.append("</span>");
	}
	 /**
	  * Function to get column value as a link or text.
	  * @param obj
	  * @return
	  * @throws TableTagException
	  */
	private String getColumnValue(PageContext pageContext ,Object obj,Locale locale, boolean isFlowRequired) throws TableTagException {
		String displayString = null;
		//To check whether type is link or text.
		if (("link".equalsIgnoreCase(type))) {
			if("true".equals(isLinkOptional)) {
				String linkrequired=(String)TableTagUtils.getInstance().helper(pageContext ,checkLinkOptionalRequired,"method",obj,locale);
				if("true".equals(linkrequired)) {
					displayString = Link.getLink(pageContext,displayname, action, parameters, obj,locale ,styleClass, isFlowRequired);
				}
				else {
					displayString = Text.getText(pageContext,displayname, image, obj,locale);
				}
			}
			else {
				displayString = Link.getLink(pageContext,displayname, action, parameters, obj,locale ,styleClass, isFlowRequired);
			}
		} else if ("text".equalsIgnoreCase(type)) {
			displayString = Text.getText(pageContext,displayname, image, obj,locale);
		}
		return displayString;
	}	
}
