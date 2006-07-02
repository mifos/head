/**

 * OfficeHierarchyBusinessProcessor.java    version: 1.0

 

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
package org.mifos.application.office.business.handlers;

import java.util.List;

import org.mifos.application.office.dao.OfficeDAO;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeHelper;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.OfficeHirerchyList;
import org.mifos.application.office.util.valueobjects.OfficeLevel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class encapsulate the business logic related to the OfficeHierarchy
 * configuration and updation of office hierarchy configuration
 * @author rajenders
 * 
 */
public class OfficeHierarchyBusinessProcessor extends MifosBusinessProcessor {
	
	// get the logger for logging
	MifosLogger officeLogger = MifosLogManager
			.getLogger(LoggerConstants.OFFICELOGGER);

	
	/**
	 * This method is called to load the master data before showing the office hierarchy 
	 * to the user .It loads all the office type which is being configured in the system
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */

	public void loadInitial(Context context) throws SystemException,
			ApplicationException {
		try {
			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());

			officeLogger.info("Loading the master data ...");
			// set the master data
			context.addAttribute(officedao.getAllOfficeLevel());
			
			//set the all the current available labels in the context to show in ui
			List officeLevels = officedao.getOfficeLevels();
			OfficeHelper.saveInContext(OfficeConstants.OLDHIERARCHYLIST,
					officeLevels, context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(
					OfficeConstants.KEYLOADINOFFICEHIERARCHYMASTERFAILED);
		}
	}

	/**
	 * This fuction will do the validation if user configure or unconfigure the
	 * given office
	 * 
	 * @param context
	 *            context objext
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context) throws SystemException,
			ApplicationException {

		try {
			OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());

			OfficeHirerchyList ohList = (OfficeHirerchyList) context
					.getValueObject();
			
			
			List selectedList = ohList.getLevelList();
			// if any office level is being changed
		
			//get the list which is there in database so that we can check whether user has 
			// selected or unselected anything 
			List officeLevels = officedao.getOfficeLevels();
			for (int i = 0; i < selectedList.size(); i++) {

				//get the current select level by user
				OfficeLevel currentLevel = (OfficeLevel) selectedList.get(i);
				
				//match that selected level with the database level
				for (int j = 0; j < officeLevels.size(); j++) {

					
					//oldlevel os database value 
					OfficeLevel oldLevel = (OfficeLevel) officeLevels.get(j);
					short oldLevelId = oldLevel.getLevelId().shortValue();
					short currentLevelId = currentLevel.getLevelId().shortValue();
					short oldConfigured=oldLevel.getConfigured().shortValue();
					short currentConfigured=currentLevel.getConfigured().shortValue();
				
					if (currentLevelId == oldLevelId) {
						// see if user has altered the state
						if (currentConfigured != oldConfigured) {
							
							officeLogger.info("Current configuration id is = "+currentConfigured+ " old configuration id is ="+oldConfigured);
							
							
							// see if he is unconfiguring it because we need to check for validation
							//if he is trying to unconfigure the office level
							if (currentConfigured == OfficeConstants.UNCONFIURE) {
								// see for the active office with this level id
								if (officedao.isOfficesActive(currentLevel
										.getLevelId())) {

									// throw exception if we have active office
									// with this levelid
									throw new OfficeException(
											OfficeConstants.KEYHASACTIVEOFFICEWITHLEVEL);
								}

							}

						}

					}
				}

			}

			// still here save the recently fetched list from the databse in
			// context so that we can use it in update
			
			

			OfficeHelper.saveInContext(OfficeConstants.OLDHIERARCHYLIST,
					officeLevels, context);
			
			
			
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(
					OfficeConstants.KEYOFFICEHIERARCHYUPDATEFAILED);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.business.handlers.MifosBusinessProcessor#update(org.mifos.framework.util.valueobjects.Context)
	 */
	@Override
	public void update(Context context) throws SystemException,
			ApplicationException {
		try {

		OfficeDAO officedao = (OfficeDAO) getDAO(context.getPath());

		officedao.updatehierarchy(context);
		} catch (ApplicationException ae) {
			throw ae;
		} catch (SystemException se) {
			throw se;
		} catch (Exception e) {
			throw new OfficeException(
					OfficeConstants.KEYOFFICEHIERARCHYUPDATEFAILED);
		}

	}
	
}
