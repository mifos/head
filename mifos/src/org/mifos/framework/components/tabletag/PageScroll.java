/**

 * PageScroll.java    version: 1.0

 

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

import java.util.ResourceBundle;

/**
 * This class renders the page scroll property of the page like previous and next links.
 */
public class PageScroll {

	/**
	 * Function is used to get the required pages on the click of previous or next link.
	 * @param current	value of the current page.
	 * @param pageSize	no of data in a single page.
	 * @param size		size of the data.
	 * @param currentFlowKey TODO
	 * @param prev		boolean value to find out whether previous is clicked or not.
	 * @param next		boolean value to find out whether next is clicked or not.
	 * @param jsp		name of the jsp to which this previous or next click will take you to.
	 * @return string	next page.
	 */
	protected static String getPages(int current,
			int pageSize, int size, String action, String currentFlowKey) {
		StringBuilder result = new StringBuilder();
		ResourceBundle resource = ResourceBundle.getBundle(TableTagConstants.PROPERTIESFILE);
		//to check the onClick status of the prev and next whether that is disable or not
		boolean prev = (current > 1) ? true : false;
		boolean next = ((current * pageSize) >= size) ? false : true;
		// Used to set the number of the start page
		int pageStart = ((current - 1) * pageSize) + 1;
		
		// Used to set the number of the end page
		int pageEnd = (current * pageSize);
		if (pageEnd > size) {
			pageEnd = size;
		}
		
		result.append("<tr>");
		
		/**
		 * to check whether previous is clicked or next is clicked 
		 * and call the respective page according to the click.
		 * if previous or next is not allowed then disable the respective link.
		 */
		
		
		if (prev) {
			result.append("<td width=\"75\" class=\"fontnormalbold\">").append(getAnchor(action, resource.getString("Previous"),"searchPrev", currentFlowKey)).append("</td>");
		} else {
			result.append("<td width=\"75\" class=\"fontnormalboldgray\">Previous</td>");
		}
		result.append("<td width=\"150\" align=\"center\" class=\"fontnormalbold\">Results "+ pageStart + "-" + pageEnd + " of " + size + " </td>");
		if (next) {
			result.append("<td width=\"75\" class=\"fontnormalbold\">").append(getAnchor(action, resource.getString("Next"),"searchNext", currentFlowKey)).append("</td>");
		} else {
			result.append("<td width=\"75\" align=\"right\" class=\"fontnormalboldgray\">Next</td>");
		}
		result.append("</tr>");
		return result.toString();
	}
	
	/**
	 * Function to get the link property on previous and next.
	 * @param hRef			page to which it is going on click.
	 * @param text			text to be displayed.
	 * @param currentFlowKey TODO
	 * @param paramName1	current. 
	 * @param paramvalue1	current page number.
	 * @param paramName2	method name.
	 * @param paramvalue2	method value(previous or next).
	 * @return
	 */
	protected static String getAnchor(String hRef, String text,String method, String currentFlowKey) {
		StringBuilder result = new StringBuilder();
		
		result.append("<a href='").append(hRef)
			  .append("?method=")
			  .append(method)
			  .append("&currentFlowKey=")
			  .append(currentFlowKey)
			  .append("'>").append(text)
			  .append("</a>");
		return result.toString();
	}
}
