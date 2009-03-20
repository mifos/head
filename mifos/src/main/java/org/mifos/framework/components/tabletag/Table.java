/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.framework.components.tabletag;

import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagException;

/**
 * This class renders the table
 */

public class Table {

	public Table() {
		super();
	}

	private Row[] row;
	
	private Path[] path;
	
	private PageRequirements pageRequirements;

	/**
	 * @return Returns the row.
	 */
	public Row[] getRow() {
		return row;
	}

	/**
	 * @return Returns the pageRequirements.
	 */
	public PageRequirements getPageRequirements() {
		return pageRequirements;
	}

	/**
	 * @param pageRequirements The pageRequirements to set.
	 */
	public void setPageRequirements(PageRequirements pageRequirements) {
		this.pageRequirements = pageRequirements;
	}

	/**
	 * @param row
	 *            The row to set.
	 */
	public void setRow(Row[] row) {
		this.row = row;
	}

	/**
	 * @return Returns the path.
	 */
	public Path[] getPath() {
		return path;
	}

	/**
	 * @param path The path to set.
	 */
	public void setPath(Path[] path) {
		this.path = path;
	}

	/**
	 * Function to get a complete row.
	 * @param obj
	 * @return string		a complete row.
	 * @throws TableTagException 
	 */
	public String getTable(PageContext pageContext ,Object obj,Locale locale, boolean isFlowRequired) throws TableTagException {
		
		StringBuilder table = new StringBuilder();
		for (int i = 0; i < row.length; i++) {
			
			//Used to store a complete row
			String foundRow = row[i].getRow(pageContext,obj,locale, isFlowRequired);
			table.append(foundRow);
			if (!((foundRow == null) || (foundRow == ""))) {
				table.append("<br>");
			}
		}
		return table.toString();
	}
	
	/**
	 * Function to get the path 
	 * @param key
	 * @return
	 */
	public Path findPath(String key) {
		for(int i=0;i<path.length;i++) {
			if(path[i].getKey().equalsIgnoreCase(key)) {
				return path[i];
			}
		}
		return null;
	}
}
