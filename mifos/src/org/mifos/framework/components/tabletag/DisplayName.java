/**

 * DisplayName.java    version: 1.0

 

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

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders the display name
 */
public class DisplayName {

	/** Used to set the value of fragmentName */
	private Fragment[] fragment;

	/** Used to set the value as bold string */
	private String bold;

	/**
	 * @return Returns the bold.
	 */
	public String getBold() {
		return bold;
	}

	/**
	 * @param bold
	 *            The bold to set.
	 */
	public void setBold(String bold) {
		this.bold = bold;
	}
	/**
	 * @return Returns the fragment.
	 */
	public Fragment[] getFragment() {
		return fragment;
	}

	/**
	 * @param fragment The fragment to set.
	 */
	public void setFragment(Fragment[] fragment) {
		this.fragment = fragment;
	}

	/**
	 * @param args
	 */
	public DisplayName() {
	}

	/**
	 * A helper method which gets the display name according 
	 * to the label using another helper method
	 * @param label		
	 * @param labelType
	 * @param obj
	 * @return
	 * @throws TableTagException 
	 */
	public String[] getDisplayName(PageContext pageContext ,Fragment[] fragment,Object obj,boolean span,Locale locale) throws TableTagException {
		
		// Used to set the value of the label if it is a simple string 
		String[] stringArray = new String[fragment.length];
		
		// Used to set the value of the label if it is a collection 
		Collection collectionObject = null;
		
		// Used to hold the value of the displayName 
		String[] string = null;

		 //Check the type of object we are getting for each label.
		 //whether that is a collection or a string.
		
		for (int i = 0; i < fragment.length; i++) {
			Object object = TableTagUtils.getInstance().helper(pageContext ,fragment[i].getFragmentName(),
					fragment[i].getFragmentType(), obj,locale);
			 //if object is a collection also then store the value of collection
			 //in collectionObject and string in a string array.
			
			if (object instanceof Collection) {
				/** Used to store the value of collection if object is a collection */
				collectionObject = (Collection) object;
				stringArray[i] = null;
			} 
			
			 //if object is a string only then store the value of string in 
			 //a string array.
			 //also check whether the  string is empty or not.
			 //if the string is empty then store null.

			else {
				String stringObject =(String)object;
				if (stringObject != null && !(stringObject.trim().equals(""))
						&& !(stringObject.trim().equalsIgnoreCase("null"))) {
					stringArray[i] = stringObject;
				} else {
					stringArray[i] = null;
				}
			}
		}
		
		 //if the collection we got is not null then build the string using 
		 //stringbuilder by writing string followed by collectionObject.
		 //if the collection is null value then string array is a single object string array.
		 //return the string.
		 
		if (collectionObject != null) {
			string = new String[collectionObject.size()];
			Iterator it = collectionObject.iterator();
			for (int k = 0; it.hasNext(); k++) {
				StringBuilder stringbuilder = new StringBuilder();
				/** Used to store the value of the collection object.
				 *  if the string is null or empty return null.
				 */
				String collValue = (String) it.next();
				if (collValue != null && !(collValue.trim().equals(""))
						&& !(collValue.trim().equalsIgnoreCase("null"))) {
					for (int i = 0; i < stringArray.length; i++) {
						stringbuilder.append(getData(fragment[i],stringArray[i] == null ? collValue
								: stringArray[i],span));
					}
					string[k] = stringbuilder.toString();
				} else {
					string[k] = null;
				}
			}
		} else {
			string = new String[1];
			StringBuilder stringbuilder = new StringBuilder();
			if (stringArray != null) {
				for (int i = 0; i < stringArray.length; i++) {
					
					stringbuilder.append(getData(fragment[i],stringArray[i],span));
				}
				string[0] = stringbuilder.toString();
			} else
				string[0] = null;
		}
		return string;
	}
	
	public String getDisplayName(PageContext pageContext ,Fragment[] fragment,Object obj,String image,Locale locale) throws TableTagException {
		
			String value =(String) TableTagUtils.getInstance().helper(pageContext ,fragment[0].getFragmentName(),
					fragment[0].getFragmentType(), obj,locale);
		return value;
	}

	private String getData(Fragment fragment,String value,boolean span) {
		StringBuilder stringBuilder=new StringBuilder();
		if (value == null || value.trim().equals("")
				|| value.trim().equals("null")) {
			return "";
		}
		if(!span) {
			return value;
		}
		
		if(!bold.equalsIgnoreCase("true")) {
			stringBuilder.append("<span class=");
			stringBuilder.append(fragment.getBold().equalsIgnoreCase("true")?"\"fontnormalbold\">"
					:"\"fontnormal\">");
			if(fragment.getItalic().equalsIgnoreCase("true")) {
				stringBuilder.append("<em>");
			}
			stringBuilder.append(value);
			stringBuilder.append("</span>");
			if(fragment.getItalic().equalsIgnoreCase("true")) {
				stringBuilder.append("</em>");
			}
		}
		else {
			stringBuilder.append(value);
		}
		return stringBuilder.toString();
	}

}
