/**

 * MifosSelect.java    version: 1

 

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
 * This Package contains the Mifos select tag and related classes
 */
package org.mifos.framework.struts.tags;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class represent the string representation of the select tag
 */
public class RawSelect {
    /** Name the componenet. */
	private String name;
    /** Size componenet. */	
	private String size;
	/** Multiple values can be selected or not. */
	private String multiple;
	/** Will hold the data for select.  */	
	//private String [] data;
	private Map data = new HashMap();
	/** Style of the select */
	private String style;
    /** Constructor for the raw Select
     * 
     */
    public RawSelect() {
        name=null;
        size="5";
        multiple=null;
        data=null;
        style=null;
    }
    /** Function get the multiple
     * @return Returns the multiple.
     */
    public String getMultiple() {
        return multiple;
    }
    /** Function set the multiple
     * @param multiple The multiple to set.
     */
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }
    /**   Function get the name
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**   Function set the name
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**   Function get the size
     * @return Returns the size.
     */
    public String getSize() {
        return size;
    }
    /**   Function set the size
     * @param size The size to set.
     */
    public void setSize(String size) {
        this.size = size;
    }
	/**
	 *  This funtion Returns the HTML representation of the button 
	 * @return String  String  representation of the button
	 */

    public String toString() {
         super.toString();
         StringBuffer results = new StringBuffer();
         results.append("<SELECT onMouseover=\"showtip(this,event,'Select the item(s)')\" onMouseOut=\"hidetip()\" onchange =\"showtip(this,event,'Select the item(s)')\" style=\"WIDTH: 136px\"" );
         prepareAttribute(results,"name",getName());
         prepareAttribute(results,"size",getSize());
         prepareAttribute(results,"multiple",getMultiple());
         prepareAttribute(results,"style",getStyle());
         results.append(">");
         MifosTagUtils mifosTagUtils = MifosTagUtils.getInstance();
         if (getData()!=null)
         {
        	 Map map = getData();
        	 Set set=map.keySet();
        	 Iterator it=set.iterator();
        	 for (int i=0; it.hasNext();i++)
             {
        		 Object object=it.next();
                 if (i==0)
                 {
                     results.append("<OPTION value=\""+object+"\"" +
             		        "selected >" + MifosTagUtils.xmlEscape(map.get(object).toString()) );
                 }
                 else {
                 results.append("<OPTION value=\""+object+"\"" +
		        " >" + MifosTagUtils.xmlEscape(map.get(object).toString()));
                 }
             }
         }
         results.append("</SELECT> ");
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
    /** Function For getting the style of Select
     * @return Returns the style.
     */
    public String getStyle() {
        return style;
    }
    /** Function For setting the style of Select
     * @param style The style to set.
     */
    public void setStyle(String style) {
        this.style = style;
    }
	/**
	 * @return Returns the data.
	 */
	public Map getData() {
		return data;
	}
	/**
	 * @param data The data to set.
	 */
	public void setData(Map data) {
		this.data = data;
	}
}
