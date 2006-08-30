/**
 
 * MifosLookUpValueTag.java    version: xxx
 
 
 
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

package org.mifos.framework.struts.tags;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This tag displays values based on the ID passed assuming that an object
 * which has the corresponding loacale specific name is set in the context 
 * with the name passed as attribute. 
 */
public class MifosLookUpValueTag extends TagSupport {
	
	public MifosLookUpValueTag() {
		super();
	}
	
	/**
	 * id corresponding to which the value has to be displayed.
	 */
	private String id;
	
	/**
	 * SearchResult Name with which the object was set in context
	 */
	private String searchResultName;
	
	/**
	 * Does the object stored in search result correspond to a seperate master table or the id 
	 * being passed directly maps to LookUp_Id
	 */
	private boolean mapToSeperateMasterTable = false;
	
	/**
	 * @return Returns the id}.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return Returns the searchResultName}.
	 */
	public String getSearchResultName() {
		return searchResultName;
	}
	
	/**
	 * @param searchResultName The searchResultName to set.
	 */
	public void setSearchResultName(String searchResultName) {
		this.searchResultName = searchResultName;
	}
	
	/**
	 * @return Returns the mapToSeperateMasterTable}.
	 */
	public boolean isMapToSeperateMasterTable() {
		return mapToSeperateMasterTable;
	}
	
	/**
	 * @param mapToSeperateMasterTable The mapToSeperateMasterTable to set.
	 */
	public void setMapToSeperateMasterTable(boolean mapToSeperateMasterTable) {
		this.mapToSeperateMasterTable = mapToSeperateMasterTable;
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		String result = "";
		try{
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside doStartTag of MifosLookUpValueTag");
			// If the value of id is blank or null return "". 
			if(null != id && !"".equals(id)){
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Checking if the id value is not null or blank and the value is : " + id);
				 result = getLookUpValue();
			}
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The value being returned is " + result);
			TagUtils.getInstance().write(pageContext, result);
		}catch(Exception e){
			//MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).error(e.getMessage(),true,null,e);
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		//pageContext.getOut().write(getLookUpValue());
		return SKIP_BODY;
	}
	
	/**
	 * Returns the html string to be displayed on the UI.
	 * @return
	 */
	private String getLookUpValue() {
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Inside getLookUpValue method of MifosLookUpValueTag");
		String valueToDisplay = "";
		// trying to retrieve the context from the session.
		//It would have been put in session in the preExecute method of MifosBaseAction. 
		Context context = (Context)SessionUtils.getAttribute(pageContext.getSession().getAttribute(Constants.PATH)+"_"+Constants.CONTEXT, pageContext.getSession());
		
		if(null != context){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Context retrieved is not null");
			valueToDisplay = getValueForId(context);
		}
		
		return valueToDisplay;
	}
	
	/**
	 * Gets the value to be displayed on the UI from the context
	 * @param context - Context in which the master data object is set.
	 * @return
	 */
	private String getValueForId(Context context){
		// get th search result from context based on the search result name
		SearchResults searchResult = context.getSearchResultBasedOnName(getSearchResultName());
		
		if(null != searchResult){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("SeacrResult obatined is not null");
			// get the vale from search result object
			Object obj = searchResult.getValue();
			// check if obj is of type EntityMaster which it would be if we helper methods in MasterDataRetriever 
			// to retrieve the master data.
			if(null != obj && obj instanceof EntityMaster){
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("value is of type entity master");
				EntityMaster entityMaster = (EntityMaster)obj;
				// gets the list from entity master
				List<LookUpMaster> lookUpMasterList = entityMaster.getLookUpMaster();
				
				if(null == lookUpMasterList){
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("LookUp master object obtained is  null");
					return "";
				}
				
				// if the master data object maps to a seperate master table compare with
				// id field in LookUpMaster else compare with LookUpId
				if(mapToSeperateMasterTable){
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Comparing with id");
					return compareWithId(lookUpMasterList);
				}else{
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("Comparing with LookUpId");
					return compareWithLookUpId(lookUpMasterList);
					
				}
			}
		}
		return null;
	}
	
	/**
	 * Compares the instance variable id with id attribute of LookUpMaster
	 * @param lookUpMasterList
	 * @return
	 */
	private String compareWithId(List<LookUpMaster> lookUpMasterList){
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The instance variable id to be compared is " + id);
		for(LookUpMaster lookUpMaster: lookUpMasterList){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The  id in LookUpMaster is  " + lookUpMaster.getId());
			if(lookUpMaster.getId().equals(new Integer(id))){
				
				return lookUpMaster.getLookUpValue(); 
			}
		}
		return "";
	}
	
	/**
	 * Compares the instance variable id with LookUpId attribute of LookUpMaster
	 * @param lookUpMasterList
	 * @return
	 */
	private String compareWithLookUpId(List<LookUpMaster> lookUpMasterList){
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The instance variable id to be compared is " + id);
		for(LookUpMaster lookUpMaster: lookUpMasterList){
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug("The  LookUpId in LookUpMaster is  " + lookUpMaster.getLookUpId());
			if(lookUpMaster.getLookUpId().equals(new Integer(id))){
				return lookUpMaster.getLookUpValue(); 
			}
		}
		return "";
	}
	
	
}
