/**

 * HierarchyManager.java    version: 1.0



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

/**
 *
 */
package org.mifos.framework.security.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.persistence.service.OfficePersistenceService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.Observer;
import org.mifos.framework.security.util.OfficeCacheView;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.security.util.SecurityEvent;
import org.mifos.framework.security.util.SecurityHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class HierarchyManager implements Observer {
	/**
	 * This would hold the office id to searchid mapping
	 */
	private static Map<Short, OfficeCacheView> hierarchyMap; 
	
	/**
	 * This would hold the singleton instance of HierarchyManager
	 */
	private static HierarchyManager hierarchyManager;

	/**
	 * Default constructor
	 */
	private HierarchyManager() {
		hierarchyMap = new HashMap<Short, OfficeCacheView>();
	}

	/**
	 * This function will return the singleton instance of the HierarchyManager
	 * class
	 *
	 * @return hm HierarchyManager instance
	 */
	public static HierarchyManager getInstance(){
		if(hierarchyManager==null)
			hierarchyManager = new HierarchyManager();
		
		return hierarchyManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.mifos.framework.security.util.Observer#handleEvent(org.mifos.framework.security.util.SecurityEvent)
	 */
	public void handleEvent(SecurityEvent e) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Map before"+hierarchyMap);
		List<OfficeCacheView> officeList = convertToOfficeCacheList((List <OfficeSearch>) e.getObject());
 		
		if (e.getEventType().equals(Constants.CREATE))
			updateMapForCreateEvent(officeList);
		else if(e.getEventType().equals(Constants.UPDATE) )
			updateMapForUpdateEvent(officeList);
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Map after "+hierarchyMap);
	}

	private void updateMapForCreateEvent(List<OfficeCacheView> officeList){
		addToMap(officeList.get(0));
	}

	private void updateMapForUpdateEvent(List<OfficeCacheView> officeList){
		for(int i=0;i<officeList.size();i++){
			synchronized (hierarchyMap) {
				hierarchyMap.remove(officeList.get(i));
				addToMap(officeList.get(i));
			}
		}
	}
	
	private void addToMap(OfficeCacheView cacheView){
		hierarchyMap.put(cacheView.getOfficeId(), cacheView);
	}
	
	private List<OfficeCacheView> convertToOfficeCacheList(List<OfficeSearch> officeList){
		List<OfficeCacheView> officeCacheList = new ArrayList<OfficeCacheView>();
		for(int i=0;i<officeList.size();i++){
			OfficeCacheView cacheView = new OfficeCacheView(officeList.get(i).getOfficeId(),officeList.get(i).getParentOfficeId(),officeList.get(i).getSearchId());
			officeCacheList.add(cacheView);
		}
		return officeCacheList;
	}
	
	public void init() throws SystemException, OfficeException {
		List<OfficeCacheView> officeList;
		try {
			officeList = ((OfficePersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Office)).getAllOffices();
		} catch (PersistenceException e) {
			throw new OfficeException(e);
		} 
		for (int i = 0; i < officeList.size(); i++)
			addToMap(officeList.get(i));
	}

	/**
	 * Returns wheter given office falls under the given user or above
	 * @param uc    usercontext object
	 * @param officeId  office id which we want to test whether it falls under or not
	 * @return short perdefind constant
	 */
	public short getOfficeLevel(UserContext uc, short officeId) {
		short userBranch = uc.getBranchId().shortValue();
		if (userBranch == officeId) {
			return Constants.BRANCH_SAME;
		} else {
			/* Look into the map now if the passed officeid's searchid on which
			   user wants to perform action starts with the user's office
			   searchid it means that office falls under that user hiererchy
			 */
			String userOfficeSearchId = hierarchyMap.get(uc.getBranchId()).getSearchId();
			String operatedOfficeSearchId = hierarchyMap.get(new Short(officeId)).getSearchId();
			
			if (operatedOfficeSearchId.startsWith(userOfficeSearchId)) {
				return Constants.BRANCH_BELOW;
			}else {
				return Constants.BRANCH_ABOVE_OR_DIFF;
			}
		}

	}

	/**
	 * This function returns all the offices falls under the given user
	 * @param branchId branch id of the given user
	 * @return the list of OfficeSearch objects which contains the id's of all the offices under the given user
	 */
	public List<OfficeSearch> getOfficesUnder(short branchId)throws SystemException, ApplicationException {
		try {
			return SecurityHelper.getPersonnelOffices(branchId);
		} catch (SystemException se) {
			throw se;
		} catch (ApplicationException ae) {
			throw ae;
		} catch (Exception e) {
			throw new SecurityException(SecurityConstants.INITIALIZATIONFAILED);
		}

	}

	/**
	 * This function will return the search id of the given branch id if anyone
	 * is intersted to find
	 * @param branchId id of the branch
	 * @return
	 */
	public String getSearchId(short branchId) {
		return hierarchyMap.get(new Short(branchId)).getSearchId().toString();
	}
	
	public Short getParentOfficeId(Short officeId) {
		return hierarchyMap.get(officeId).getParentOfficeId();
	}
}
