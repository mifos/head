/**

 * OfficeHelper.java    version: 1.0

 

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
package org.mifos.application.office.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.master.util.valueobjects.OfficeLevelMaster;
import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class has hepler function which are called during verious office 
 * functions
 * @author rajenders
 *
 */
public class OfficeHelper {
	
	private static MifosLogger officeLogger =null;  

	/*
	 * This function is called to generate the searchId for the office 
	 */
	public static MifosLogger getLogger(){
		if(officeLogger==null) officeLogger=MifosLogManager.getLogger(LoggerConstants.OFFICELOGGER);
		return officeLogger;
	}
	public static  String generateSerachId(Context context) throws SystemException, ApplicationException 
	{
		//Security has office id to searchid map with it
		OfficeDAO officedao = new OfficeDAO();
		Office office = (Office)context.getValueObject();

		
		int noOfChildren = officedao.getParentChildern(office.getParentOffice().getOfficeId());

		getLogger().debug(" getting the parent office ");
		Office parentOffice = officedao.getOffice(office.getParentOffice().getOfficeId());
		getLogger().debug("getting the parent office sucessfully ");	
		
		if(null!=parentOffice)
		{
			String searchId = parentOffice.getSearchId();
			int number = noOfChildren+1;
			searchId+=".";
			searchId+=number;
			// set the parent office so that we need not fetch it again
			office.setParentOffice(parentOffice);
			return searchId;
		}
		else
		{
			//TODO key needed
			throw new OfficeException();
		}

		
	}
	
	/**
	 * This function would return the search results based on the passed name
	 * and value
	 * 
	 * @param name
	 *            name of the search result
	 * @param value
	 *            value of the search result
	 * @return
	 */
	public static SearchResults getSearchResutls(String name, Object value) {
		SearchResults sr = new SearchResults();
		sr.setResultName(name);
		sr.setValue(value);
		return sr;
	}
	/**
	 * This function will save the specified object into the context 
	 * @param name name of the object we want to save 
	 * @param obj object that we want to save 
	 * @param context context object
	 */
	public static void saveInContext(String name, Object obj, Context context) {
		
		context.removeAttribute(name);
		SearchResults result = OfficeHelper.getSearchResutls(
				name, obj);
		context.addAttribute(result);
	}
	
	/**
	 * This function updates the officeLevel list by removing the given level form the list 
	 * @param context
	 * @param level
	 */
	public static void UpdateOfficeLevelList( Context context,int level)
	{
		SearchResults oldResults = context
		.getSearchResultBasedOnName(OfficeConstants.OFFICELEVELLIST);
		List levelList=null;
		if(null!=oldResults )
		{
			 levelList = (List)oldResults.getValue();
			
			for( int i=0 ;i<levelList.size();i++)
			{
				if ( ((OfficeLevelMaster)levelList.get(i)).getLevelId().shortValue()!=level)
				{
					levelList.remove(i);
					i--;
				}
			}
		}
		
		
		saveInContext(OfficeConstants.OFFICELEVELLIST,levelList,context);
	}
	
	/**
	 * This function updates the officeLevel list for create office as we 
	 * can not create head office so we remove it from the list 
	 * @param context
	 */
	public static void UpdateOfficeLevelList( Context context)
	{
		SearchResults oldResults = context
		.getSearchResultBasedOnName(OfficeConstants.OFFICELEVELLIST);
		List levelList=null;
		if(null!=oldResults )
		{
			 levelList = (List)oldResults.getValue();
			
			for( int i=0 ;i<levelList.size();i++)
			{
				
				short level= ((OfficeLevelMaster)levelList.get(i)).getLevelId().shortValue();
				if (  level==OfficeConstants.HEADOFFICE)
				{
					levelList.remove(i);
					i--;
				}
			}
		}
		
		
		saveInContext(OfficeConstants.OFFICELEVELLIST,levelList,context);
	}
	
	public static void updateSearchIds( short office,String searchId,short oldParent,short newParent,List<OfficeSearch> offices) throws ApplicationException
	{
		try
		{
		
		//first build the childernMap 
		Map<Short,List> childernMap = new HashMap<Short,List>();
		for( int i=0;i<offices.size();i++)
		{
			OfficeSearch os = offices.get(i);
			Short parent = os.getOfficeId();
			childernMap.put(parent,getChildern(offices,parent,os.getSearchId()));
		}
		//remove the child from the old parent get the list of childern for the old office
		List<OfficeSearch> oldChildern = childernMap.get(oldParent);
		OfficeSearch cos = null;
		if ( oldChildern.size()>0)
		{
			getLogger().debug("Search id of the office which we are modifying "+searchId);
			int beginIndex = searchId.lastIndexOf(".")+1;
			getLogger().debug("Begin index is "+beginIndex);
			getLogger().debug("Substring is  "+searchId.substring(beginIndex));
			short officeLastNumber = Short.valueOf(searchId.substring(beginIndex));
			
			//copy the reference if this is the current office
			for (int j = 0 ;j<oldChildern.size();j++ )
			{
				OfficeSearch child = oldChildern.get(j);
				String childSearchId = child.getSearchId();
				short officeChildLastNumber = Short.valueOf(childSearchId.substring(beginIndex));
				
				if(officeChildLastNumber==officeLastNumber)
				{
					cos=child;
					oldChildern.remove(j);
					j--;
				}
				else if ( officeChildLastNumber>officeLastNumber)
				{
					//update the searchId of this office
					String childId = childSearchId.substring(0,beginIndex-1);
					childId=childId+"."+(officeChildLastNumber-1);
					child.setSearchId(childId);
					UpdateIds(child,childernMap);
				}
			}
		}
		//time to add this office to new parent
		String newParentSearchId = getSearchId(newParent,offices);
		List<OfficeSearch> newChildern = childernMap.get(newParent);
		String officeNewSearchId = newParentSearchId + "."+(newChildern.size()+1);
		cos.setSearchId(officeNewSearchId);
		UpdateIds(cos,childernMap);
		}
		catch(NumberFormatException nfc)
		{
			throw new OfficeException(nfc);
		}
	}
	private static List<OfficeSearch> getChildern(List<OfficeSearch> offices,Short parent,String parentSearchId)
	{
		short parentId = parent.shortValue();
		List <OfficeSearch> childern = new ArrayList<OfficeSearch>();
		//make the  regular expression
		String regEx = "^"+parentSearchId.replace(".","\\.")+"\\.[0-9]*";
		getLogger().debug("value of regular expressio is "+regEx);
		for( int i=0;i<offices.size();i++)
		{
			OfficeSearch os = offices.get(i);
			short childId = os.getOfficeId().shortValue();
			String childSearchId = os.getSearchId();
			if( parentId!=childId)
			{
				if (childSearchId.matches(regEx))
				{
					childern.add(os);
				}
			}
		}
		return childern;
	}
	
	private static void UpdateIds(OfficeSearch os ,Map childernMap)
	{
		List childern =(List) childernMap.get(os.getOfficeId());
		for (int i = 0; i < childern.size(); i++) {
			OfficeSearch  child =(OfficeSearch) childern.get(i);
			//add new searchId
			child.setSearchId(os.getSearchId()+"."+(i+1));
			UpdateIds(child,childernMap);
		}
	}
	
	private static String getSearchId(short officeId,List<OfficeSearch> offices)
	{
		for (int i = 0; i < offices.size(); i++) {
			OfficeSearch  child =offices.get(i);
			if ( child.getOfficeId().shortValue()==officeId)
			{
				return child.getSearchId();
			}
		}	
		
		return null;
	}
}
