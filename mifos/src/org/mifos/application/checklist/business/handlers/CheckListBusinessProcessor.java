/**

 * CheckListBusinessProcessor  version: 1.0



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

package org.mifos.application.checklist.business.handlers;

import java.util.Date;
import java.util.Set;
import org.mifos.application.accounts.util.valueobjects.AccountCheckList;
import org.mifos.application.checklist.dao.CheckListDAO;
import org.mifos.application.checklist.util.helpers.CheckListHelper;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.checklist.util.valueobjects.CheckList;
import org.mifos.application.checklist.util.valueobjects.CheckListDetail;
import org.mifos.application.checklist.util.valueobjects.CustomerCheckList;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;

/**
 * CheckListBusinessProcessor contains the business logic for checklist 
 * @author imtiyazmb
 *
 */

public class CheckListBusinessProcessor extends MifosBusinessProcessor {

	public CheckListBusinessProcessor() {
		super();
	}

	/**
	 *called when creating checklist page loads
	 *@param context
	 *@throws SystemException
	 *@throws ApplicationException
	 **/
	public void loadInitial(Context context) throws SystemException,
			ApplicationException {
		CheckListDAO checklistDao = (CheckListDAO) getDAO(context.getPath());
		Short localeId=getUserLocaleId(context.getUserContext());
		checklistDao.loadMasterdata(context,localeId);

	}

	/** sets either customerchecklist or accountchecklist depending upon category 
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context) throws SystemException,
			ApplicationException {
		CheckList checkList = (CheckList) context.getValueObject();
		Short type = Short.valueOf(context.getSearchResultBasedOnName("Type")
				.getValue().toString());
		UserContext userContext = context.getUserContext();
		Short localeId = userContext.getLocaleId();
		SupportedLocales supportedLocales = new SupportedLocales();
		supportedLocales.setLocaleId(localeId);
		checkList.setSupportedLocales(supportedLocales);
		checkList.setChecklistStatus(Short.valueOf("1"));		
		checkList.setCreatedBy(context.getUserContext().getId());		
		checkList.setCreatedDate( new Date(System.currentTimeMillis()));

		Set<CheckListDetail> checkListDetailSet = checkList
				.getChecklistDetailSet();

		for (CheckListDetail checkListDetail : checkListDetailSet) {
			checkListDetail.setCheckList(checkList);
			checkListDetail.setAnswerType(Short.valueOf("1"));
		}

		CustomerCheckList customerCheckList = checkList.getCustomerChecklist();
		if (type == 0) {
			customerCheckList.setCheckList(checkList);
			checkList.setAccountCheckList(null);
		}

		AccountCheckList accountCheckList = checkList.getAccountCheckList();
		if (type == 1) {

			accountCheckList.setCheckList(checkList);
			checkList.setCustomerChecklist(null);
		}

	}

	/** gets the checklist details*/
	/**
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getCheckListNames(Context context) throws SystemException,
			ApplicationException {
		Short localeId=getUserLocaleId(context.getUserContext());
		((CheckListDAO) getDAO(context.getPath()))
				.getCheckListMasterdata(context,localeId);

	}

	/** gets the status of parent category*/
	/**
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadParent(Context context) throws SystemException,
			ApplicationException {
		CheckListDAO checklistDao = (CheckListDAO) getDAO(context.getPath());
		Short localeId=getUserLocaleId(context.getUserContext());
		checklistDao.getStatus(context,localeId);

	}

	/** gets the details of particular checklist*/
	/**
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void manageInitial(Context context) throws SystemException,
			ApplicationException {

		try {
			CheckListDAO checklistDao = (CheckListDAO) getDAO(context.getPath());
			Short localeId=getUserLocaleId(context.getUserContext());
			checklistDao.loadMasterdata(context,localeId);
			if (null != context) {
				Object obj = context.getSearchResultBasedOnName(
						"CheckListParent").getValue();

				if (null != obj) {
					if (obj instanceof AccountCheckList) {

						AccountCheckList accountCheckList = (AccountCheckList) obj;
						CheckListHelper.saveInContext("Level", accountCheckList
								.getAccountTypeId(), context);

					}
					if (obj instanceof CustomerCheckList) {

						CustomerCheckList customerCheckList = (CustomerCheckList) obj;
						CheckListHelper.saveInContext("Level",
								customerCheckList.getLevelId(), context);

					}
				}

				checklistDao.getStatus(context,localeId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *validation for destination state of corresponding type
	 *@param context
	 *@throws SystemException
	 *@throws ApplicationException
	 **/
	public void previewInitial(Context context) throws SystemException,
			ApplicationException {

		CheckListDAO checklistDao = (CheckListDAO) getDAO(context.getPath());
		checklistDao.validateState(context);

	}
	
	/** returns localeId
	 * @param userContext
	 * @return
	 */
	private Short getUserLocaleId(UserContext userContext) {
		Short localeId = 1;
		if (null != userContext) {
			localeId = userContext.getLocaleId();
		}	
		if (null == localeId) {
			localeId = Short.valueOf(CheckListConstants.LOCALEID);
		}
		return localeId;
	}
}
