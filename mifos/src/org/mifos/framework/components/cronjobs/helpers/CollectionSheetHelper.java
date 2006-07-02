/**

 * CollectionSheetHelper.java    version: 1.0

 

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
package org.mifos.framework.components.cronjobs.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.collectionsheet.persistence.service.CollectionSheetPersistenceService;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.PersistenceServiceName;

/**
 * This is the helper class which is invoked by the collection sheet task.
 * The execute method of this class is called by the task.This class generates the collection sheet
 * for the next day , it does that by adding a day to the date when it runs.
 * But it would not take care of the situation where lets say it is scheduled to run at 8:00 p.m.
 * every day and generate collection sheet data for the next date but lets say due to some reason this gets delayed 
 * and runs after 0:00 hrs in which it would generate collection sheet data for the next day and todays data would 
 * be lost.
 * @author ashishsm
 *
 */
public class CollectionSheetHelper extends TaskHelper {

	private CollectionSheetBO getNewCollectionSheet(Date currentDate){
		//Date meetingDate = new Date(currentDate.getTime());
		//meetingDate.setDate(currentDate.getDate() + 1);
		Calendar  meeting = new GregorianCalendar();
		meeting.setTimeInMillis(currentDate.getTime());
		meeting.roll(Calendar.DATE,1);
		
		CollectionSheetBO collectionSheet = new CollectionSheetBO();
		collectionSheet.setCollSheetDate(new Date(meeting.getTimeInMillis()));
		collectionSheet.setRunDate(currentDate);
		return collectionSheet;
	}

	/**
	 * This method is called by the collectionsheet task to generate collection sheet for the 
	 * time being passed.This method opens the session when it starts generating the collection sheet
	 * and closes it when the generation is complete hence the session is opened for the entire process.
	 * The reason this should not be a problem is that this task runs at night when there are no users connected
	 * using the system so this should not be a problem.  This generates the collection sheet data, it first creates a collection sheet object
	 * populates its fileds and saves it to the data base with status as started.Finally saves it in the database with the status successful
	 * or if there was some exception it tries to update the database with status failed.
	 * @see org.mifos.framework.components.cronjobs.TaskHelper#execute(long)
	 */
	public void execute(long timeInMillis) {
		Date currentDate = new Date(timeInMillis);
		
		CollectionSheetBO collectionSheet =  getNewCollectionSheet(currentDate);
		
		
		try{
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			collectionSheet.create();
			generateCollectionSheetForDate(collectionSheet);
			collectionSheet.update(CollectionSheetConstants.COLLECTIONSHEETGENERATIONSUCCESSFUL);
			HibernateUtil.getTransaction().commit();
		}catch(Exception e){
			// if there is an exception roll back the transaction, close the session and open a new one.
			HibernateUtil.getTransaction().rollback();
			HibernateUtil.closeSession();
			
			HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			collectionSheet = getNewCollectionSheet(currentDate);
			MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).error("Collection sheet generation failed");
			try{
				collectionSheet.update(CollectionSheetConstants.COLLECTIONSHEETGENERATIONSFAILED);
				HibernateUtil.getTransaction().commit();
			}catch(SystemException se){
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).error("Collection sheet generation failed also the status could not be updated");
			}catch(ApplicationException ae){
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).error("Collection sheet generation failed also the status could not be updated");
			}
			
		}finally{
			
			HibernateUtil.closeSession();
		}

	}
	
	/**
	 *It queries the database for valid customers which have the meeting date tomorrow and populates its 
	 * corresponding fields with the data.
	 * @param currentDate
	 */
	private void generateCollectionSheetForDate(CollectionSheetBO collectionSheet )throws SystemException,ApplicationException{
		
			CollectionSheetPersistenceService collSheetPerService = (CollectionSheetPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.CollectionSheet);
			
			List<AccountActionDateEntity> accountActionDates = collSheetPerService.getCustFromAccountActionsDate(collectionSheet.getCollSheetDate());
			
			MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After retrieving account action date objects for next meeting date. ");
			if(null != accountActionDates && accountActionDates.size() > 0){
				collectionSheet.populateAccountActionDates(accountActionDates);
				
			}
			
			List<LoanBO> loanBOs = collSheetPerService.getLnAccntsWithDisbursalDate(collectionSheet.getCollSheetDate());
			MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After retrieving loan accounts due for disbursal");
			
			if(null != loanBOs && loanBOs.size()>0){
				collectionSheet.addLoanDetailsForDisbursal(loanBOs);
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After processing loan accounts which had disbursal due.");
			}
			
			if(null != collectionSheet.getCollectionSheetCustomers() && collectionSheet.getCollectionSheetCustomers().size()>0){
				collectionSheet.updateCollectiveTotals();
				MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug("After updating collective totals");
			}
			
		
	}
	
	
	

}
