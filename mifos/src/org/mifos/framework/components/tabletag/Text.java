/**

 * Text.java    version: 1.0

 

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

package org.mifos.framework.components.tabletag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders text if display name is text only.
 * @author rohitr
 * 
 */
public class Text {

	/**
	 * Function to get the display name as text.
	 * @param displayname		name to be displayed.
	 * @param image				image to be put.
	 * @param obj
	 * @return string			returns the text.
	 * @throws TableTagException 
	 */
	public static String getText(PageContext pageContext ,DisplayName displayname, String image,
			Object obj,Locale locale) throws TableTagException {
		if (null != image && image.equals("true")) {
			String name = displayname.getDisplayName(pageContext ,
					displayname.getFragment(),obj,image,locale);
			return getImage(obj ,name);
		}

		// Used to get the string array of display name 
		String[] name = displayname.getDisplayName(pageContext ,
				displayname.getFragment(),obj,true,locale);
		 
		// to check whether any image is there or not.
		 // if image is there then get image

		
		
		String bold=displayname.getBold();
		 // otherwise display text.
		return getDisplayText(name,bold);
	}
	
	//to get Image
	private static String getImage(Object obj, String name) throws TableTagException{
		StringBuilder stringbuilder = new StringBuilder();
		Method method = null;
		Object customerType = null;
		String textValue = null;
		String imagePath = null;
		try {
			method = obj.getClass().getDeclaredMethod("getCustomerType",(Class[]) null);
			customerType = (Object) method.invoke(obj, (Object[]) null);
		} catch (NoSuchMethodException nsme) {
			throw new TableTagException(nsme.getMessage());
		} catch (IllegalArgumentException iae) {
			throw new TableTagException(iae);
		} catch (IllegalAccessException iae) {
			throw new TableTagException(iae);
		} catch (InvocationTargetException ite) {
			throw new TableTagException(ite);
		}
		
		ResourceBundle resource = ResourceBundle.getBundle(TableTagConstants.PROPERTIESFILE);		
		if(customerType != null && (customerType.toString().equals("4") || customerType.toString().equals("5")))
		{
			textValue = resource.getString("loanaccount_stateid_" + name);
			imagePath = resource.getString("loanaccount_imageid_" + name);
		}
		else if(customerType != null && (customerType.toString().equals("6") || customerType.toString().equals("7") || customerType.toString().equals("8")))
		{
			textValue = resource.getString("savings_stateid_" + name);
			imagePath = resource.getString("savings_imageid_" + name);
		}
		else
		{
		textValue = resource.getString("value_" + name);
		imagePath = resource.getString("image_" + name);
		}
		stringbuilder.append("<span class=\"fontnormal\">")
					 .append("&nbsp;")
					 .append("<img src=")
					 .append(imagePath)
					 .append(" width=\"8\" height=\"9\">")
					 .append("</span>")
					 .append("<span class=\"fontnormal\">")
					 .append("&nbsp;")
					 .append(textValue)
					 .append("</span>");
		return stringbuilder.toString();
	} 
	
	//to get Text
	private static String getDisplayText(String[] name,String bold) {
		StringBuilder stringbuilder = new StringBuilder();
		for(int i=0;i<name.length;i++) {
			if (name[i] == null || name[i].trim().equals("")
					|| name[i].trim().equals("null")) {
				return "";
			}
			stringbuilder.append("<span class=");
			stringbuilder.append(bold.equalsIgnoreCase("true")?"\"fontnormalbold\">"
					:"\"fontnormal\">");
			stringbuilder.append(name[i]+"</span>");
			stringbuilder.append((i==(name.length-1))?"":",");
		}
		return stringbuilder.toString();
	}
}
