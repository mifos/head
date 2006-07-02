/**

 * Link.java    version: 1.0

 

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

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders the link to the display name.
 * @author rohitr
 * 
 */
public class Link {
	
	

	/**
	 * 
	 * @param displayname	name to be displayed
	 * @param action		action to be taken
	 * @param param			parameters passed for that action
	 * @param obj			object got from the user
	 * @return string		string as a link.
	 * @throws TableTagException 
	 * @throws UnsupportedEncodingException 
	 */
	public static String getLink(PageContext pageContext ,DisplayName displayname, String action,
			Parameters param, Object obj,Locale locale ,String styleClass) throws TableTagException {
		
		//Used to get the string array of display name 
		String[] name = displayname.getDisplayName(pageContext,
				displayname.getFragment(),obj,false,locale);
		
		// Used to get the display name as bold string  
		String bold = displayname.getBold();
		
		// Used to get the string array of parameters 
		String[] parameters = param.getParameters(pageContext,param.getParam(), obj,locale);

		return createLink(name,parameters,bold,action ,styleClass);

	}
	
	//Used to create the link 
	private static String createLink(String[] name,String[] parameters,
			String bold,String action ,String styleClass) {
		StringBuilder stringbuilder = new StringBuilder();
		for (int i = 0; i < name.length; i++) {
			if (name[i] == null || name[i].trim().equals("")
					|| name[i].trim().equals("null")) {
				return "";
			}			
			if(styleClass != null && styleClass.equals("fontnormalbold") && bold.equalsIgnoreCase("true"))
			{		
				stringbuilder.append("<span class=");
				stringbuilder.append("\""+styleClass+"\">");
				stringbuilder.append("<a href= ");
				stringbuilder.append("\"" + action + "?" + parameters[i] );
				stringbuilder.append("\">");
				stringbuilder.append(" " + name[i] + "</a></span>");
			}
			else if(styleClass != null && styleClass.equals("headingblue") && bold.equalsIgnoreCase("true"))
			{			
				stringbuilder.append("<span class=");
				stringbuilder.append("\""+ styleClass +"\">"); 
				stringbuilder.append("<a href= ");
				stringbuilder.append("\"" + action + "?" + parameters[i] +"\"" );	
				stringbuilder.append("class=\""+styleClass+"\"");
				stringbuilder.append(">");
				stringbuilder.append(" " + name[i] + "</a></span>");
			}
			else
			{			
				stringbuilder.append("<span>");				
				stringbuilder.append("<a href= ");
				stringbuilder.append("\"" + action + "?" + parameters[i] );
				//stringbuilder.append("class=");
				//stringbuilder.append(bold.equalsIgnoreCase("true")?"\"headingblue\">" :"\"\">");
				stringbuilder.append("\">");
				stringbuilder.append(" " + name[i] + "</a></span>");					
			}
			stringbuilder.append((i == (name.length - 1)) ? "" : ",");			
		}
		return stringbuilder.toString();
	}
}
