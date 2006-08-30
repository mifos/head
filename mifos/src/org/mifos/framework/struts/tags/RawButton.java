/**

 * RawButton.java    version: 1

 

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
/*
 * Created on Aug 3, 2005
 * class to create the html representation of the button
 */
package org.mifos.framework.struts.tags;

/**
 * class RawButton represent the html button on the screen 
 */
public class RawButton {
	/**
	 * Default no arg constructor initialise the members mainley the type and value
	 */
	public RawButton() {
		name=null;
		type="button";
		value="Add >>";
		disabled=null;
		title=null;
		style=null;
		id=null;
		onclick=null;
		ondblclick=null;
	}

	/** Name of the control */
	private String name;
	/** Type of input  */
	private String type;
	/** value of the input */
	private String value;
	 /** Component is disabled. */
	private String disabled;
	/** Title associated with component. */
	private String title;
	/** Style attribute associated with component. */
	private String style;
	/** Identifier associated with component.  */
	private String id;
	/** Onclick mouse handler. */
	private String onclick;
	/** ondblclick mouse handler. */
	private String ondblclick;
	/**   Function get the ondblclick
	 * @return Returns the ondblclick.
	 */
	public String getOndblclick() {
		return ondblclick;
	}
	/**   Function set the ondblclick
	 * @param ondblclick The ondblclick to set.
	 */
	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}
	/**  Function get the style
	 * @return Returns the style.
	 */
	public String getStyle() {
		return style;
	}
	/**  Function set the style
	 * @param style The style to set.
	 */
	public void setStyle(String style) {
		this.style = style;
	}
	/**  Function get the title
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**  Function set the title
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**  Function get the type
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**  Function set the type
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/** Function get the value
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/** Function set the value
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/* This funtion Returns the HTML representation of the button 
	 */
	public String toString() {
		super.toString();
		StringBuffer results = new StringBuffer();
		results.append("<INPUT ");
		prepareAttribute(results,"name",getName());
		prepareAttribute(results,"type",getType());
		prepareAttribute(results,"value",getValue());
		prepareAttribute(results,"disabled",getDisabled());
		prepareAttribute(results,"title",getTitle());
		prepareAttribute(results,"style","width:65px");
		prepareAttribute(results,"class","insidebuttn");
		prepareAttribute(results,"id",getId());
		prepareAttribute(results,"onclick",getOnclick());
		prepareAttribute(results,"ondblclick",getOndblclick());
		results.append("onMouseover=\"showtip(this,event,'Click To move the selected item')\" onMouseout=\"hidetip()\" ></INPUT>");
		return results.toString();
		
		
	}
	/*  Helper function to append the string representaion of attributes to 
	 *  string buffer results
	 * @param handlers:  String buffer to hold string representation of attributes
	 * @param name: name of the attribute
	 * @param value: value of attribute
	 */
	
	   private void prepareAttribute(StringBuffer handlers, String name, Object value) {
        if (value != null) {
            handlers.append(" ");
            handlers.append(name);
            handlers.append("=\"");
            handlers.append(value);
            handlers.append("\"");
        }
    }
	/** Function get the disabled
	 * @return Returns the disabled.
	 */
	public String getDisabled() {
		return disabled;
	}
	/** Function set the disabled
	 * @param disabled The disabled to set.
	 */
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	/**Function get the id
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/** Function set the id
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/** Function get the name
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/** Function set the name
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/** Function get the onclick
	 * @return Returns the onclick.
	 */
	public String getOnclick() {
		return onclick;
	}
	/** Function set the  onclick
	 * @param onclick The onclick to set.
	 */
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
}
