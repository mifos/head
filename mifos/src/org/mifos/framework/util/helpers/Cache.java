/**

 * Cache.java    version: 1.0



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
package org.mifos.framework.util.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mifos.framework.components.tabletag.TableTagConstants;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.hibernate.helper.QueryResult;

public class Cache {

	/** Variable to hold the data with key-value pair */
	private Map cacheMap = new HashMap();

	/** Variable to hold the page size which we are taking from resoursce bundle */
	int pageSize = Integer.parseInt(TableTagConstants.MIFOSTABLE_PAGESIZE);

	/** Variable to hold the size of the data */
	private int size = 0;


	/** Variable to hold the data */
	QueryResult data = null;

	/** Variable to hold the start page number */

	/** Variable to hold the last page number */


	/** Variable to hold the number of pages */
	int noOfPages = 0;

	/**
	 * @return Returns the cacheMap.
	 */
	public Map getCacheMap() {
		return cacheMap;
	}

	/**
	 * @param cacheMap
	 *            The cacheMap to set.
	 */
	public void setCacheMap(Map cacheMap) {
		this.cacheMap = cacheMap;
	}

	/**
	 * @return Returns the size.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size
	 *            The size to set.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Default Constructor
	 */
	public Cache() {
		super();
	}

	/**
	 * Overriding constructor
	 * @param listObject	object we are getting from user.
	 * @param key			key of the session.
	 * @throws HibernateSearchException
	 */

	public Cache(Object listObject) throws HibernateSearchException {
		this.data = (QueryResult)listObject;
		size = data.getSize();
		// System.out.println("size of the Query object!!!!!!!!!!!!!!!!!!!!!!!!!"+size);
		/**
		 * fixing the no of pages according to size of the list and pagesize.
		 */
		noOfPages = (size % pageSize == 0) ? (size / pageSize)
				: ((size / pageSize) + 1);

		/** Calling helper method to get the data */
		getData();
	}

	/** Function to get the data
	 * @throws HibernateSearchException */

	private void getData() throws HibernateSearchException {
		int start = 0;
		/** if size of the data is greater than 50 then store first 50 data
		 *  in the cache.
		 *  otherwise store all data.
		 */
		
		addFromObject(start,pageSize);
		/*
		int end=(noOfPages>5?5:noOfPages);
			for (int i = 1; i <= end; i++) {
				addFromObject(start,i);
				start = ((start + pageSize) < size) ? (start + pageSize) : size;
			}
		*/
		
	}

	/**
	 * Function to set the cache repository size.
	 * we are putting 5 pages in cache anytime.
	 * @param current		current page number.
	 * @param methodValue	method value whether it is previous or next.
	 * @throws HibernateSearchException
	 */
	public List getList(int current,String methodValue) throws HibernateSearchException {

		/** if current page is 1 or 2 then we are not doing anything with
		 * the repository. but if user is coming to page-3 using previous
		 * key then remove the value of page-6 from the cache.
		 * for any other page number call another helper method to add and
		 * remove the page which is out of bound.
		 */
		if((current==0||current==1||current==2)) {
		}

		else {
			if(current==3) {
				if("previous".equalsIgnoreCase(methodValue)) {
					removeFromCacheMap(current+3);
					if(!(cacheMap.containsKey(Integer.valueOf(1)))) {
						addFromObject(0,1);
					}
				}
			}
		else {
			remove(current,methodValue);
			add(current,methodValue);

			 }
		}

		return getDataFromCache(current);
	}

	/**
	 * A helper method to remove the page which is not required.
	 * @param key		key of the cacheMap.
	 * @param method	previous or next.
	 */
	private void remove(int key,String method) {
		if("previous".equalsIgnoreCase(method)) {
			key = (key+3);
		}
		else {
			if("next".equalsIgnoreCase(method)) {
				key = (key-3);
			}
		}
		removeFromCacheMap(key);
	}

	/**
	 * A helper method to add the page which is required.
	 * @param key		key of the cacheMap.
	 * @param method	previous or next.
	 * @throws HibernateSearchException
	 */
	private void add(int key,String method) throws HibernateSearchException {
		int start = 0;
		if("previous".equalsIgnoreCase(method)) {
			start = (key-3)*pageSize;
			key = key-2;
		}
		else {
			if("next".equalsIgnoreCase(method)) {
				start = (key+1)*pageSize;
				key=key+2;
			}
		}
		start = (start<size?start:size);
		addFromObject(start,key);
	}

	private void addFromObject(int start,int key) throws HibernateSearchException {
		
		cacheMap.put(new Integer(start), data.get(start,pageSize));
		// System.out.println("start="+start);
	}

	private void removeFromCacheMap(int key) {
		Integer keyRemove=Integer.valueOf(key);
		if(cacheMap.containsKey(keyRemove)) {
			cacheMap.remove(keyRemove);
		}
	}

	private List getDataFromCache(int current) throws HibernateSearchException {
		Integer key = current;
		if(cacheMap.containsKey(key)) {
			return (List)cacheMap.get(key);
		}
		else {
			if(current<=noOfPages) {
				int start=(current-1)*pageSize<size?(current-1)*pageSize:size;
				return data.get(start,pageSize);
			}
		}
		return null;
	}

}
