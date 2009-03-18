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

package org.mifos.framework.components.batchjobs.helpers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * This is the helper class which is invoked by the collection sheet task. The
 * execute method of this class is called by the task.This class generates the
 * collection sheet for the next day , it does that by adding a day to the date
 * when it runs. But it would not take care of the situation where lets say it
 * is scheduled to run at 8:00 p.m. every day and generate collection sheet data
 * for the next date but lets say due to some reason this gets delayed and runs
 * after 0:00 hrs in which it would generate collection sheet data for the next
 * day and todays data would be lost.
 */
public class CollectionSheetHelper extends TaskHelper {

	public CollectionSheetHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	public CollectionSheetBO getNewCollectionSheet(Date currentDate) throws PersistenceException {
		Calendar meeting = new GregorianCalendar();
		meeting.setTimeInMillis(currentDate.getTime());
		meeting.roll(Calendar.DATE, getDaysInAdvance());

		CollectionSheetBO collectionSheet = new CollectionSheetBO();
		collectionSheet.setCollSheetDate(new Date(meeting.getTimeInMillis()));
		collectionSheet.setRunDate(currentDate);
		return collectionSheet;
	}

	/**
	 * This method is called by the collectionsheet task to generate collection
	 * sheet for the time being passed.This generates the collection sheet data,
	 * it first creates a collection sheet object populates its fields and saves
	 * it to the data base with status as started.Finally saves it in the
	 * database with the status successful or if there was some exception it
	 * tries to update the database with status failed.
	 */
	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		List<String> errorList = new ArrayList<String>();
		Date currentDate = new Date(timeInMillis);
		CollectionSheetBO collectionSheet = null;
		try {
			collectionSheet = getNewCollectionSheet(currentDate);
			collectionSheet.create();
			generateCollectionSheetForDate(collectionSheet);
			collectionSheet
					.update(CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);
			StaticHibernateUtil.commitTransaction();
		} catch (Exception e) {
			// if there is an exception roll back the transaction, close the
			// session and open a new one.
			StaticHibernateUtil.rollbackTransaction();
			StaticHibernateUtil.closeSession();
			errorList.add("Coll Sheet Date"
					+ collectionSheet.getCollSheetDate() + "Coll Sheet Date"
					+ collectionSheet.getCollSheetID() + "Coll Sheet Run Date"
					+ collectionSheet.getRunDate());
			try {
				collectionSheet = getNewCollectionSheet(currentDate);
				collectionSheet
						.update(CollectionSheetConstants.COLLECTION_SHEET_GENERATION_FAILED);
				StaticHibernateUtil.commitTransaction();
			} catch (Exception ae) {
				StaticHibernateUtil.rollbackTransaction();
				MifosLogManager
						.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
						.error(
								"Collection sheet generation failed also the status could not be updated");
			}
		} finally {
			StaticHibernateUtil.closeSession();
		}
		if (errorList.size() > 0)
			throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
	}

	/**
	 * It queries the database for valid customers which have the meeting date
	 * tomorrow and populates its corresponding fields with the data.
	 */
	private void generateCollectionSheetForDate(
			CollectionSheetBO collectionSheet) throws SystemException,
			ApplicationException {
		List<AccountActionDateEntity> accountActionDates = new CollectionSheetPersistence()
				.getCustFromAccountActionsDate(collectionSheet
						.getCollSheetDate());
		MifosLogManager
				.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
				.debug(
						"After retrieving account action date objects for next meeting date. ");
		if (null != accountActionDates && accountActionDates.size() > 0) {
			collectionSheet.populateAccountActionDates(accountActionDates);
		}
		List<LoanBO> loanBOs = new CollectionSheetPersistence()
				.getLnAccntsWithDisbursalDate(collectionSheet
						.getCollSheetDate());
		MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER).debug(
				"After retrieving loan accounts due for disbursal");
		if (null != loanBOs && loanBOs.size() > 0) {
			collectionSheet.addLoanDetailsForDisbursal(loanBOs);
			MifosLogManager
					.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
					.debug(
							"After processing loan accounts which had disbursal due.");
		}
		if (null != collectionSheet.getCollectionSheetCustomers()
				&& collectionSheet.getCollectionSheetCustomers().size() > 0) {
			collectionSheet.updateCollectiveTotals();
			MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
					.debug("After updating collective totals");
		}
	}

	/**
	 * Fetches number of days in advance the collection sheet should be
	 * generated.
	 */
	public static int getDaysInAdvance() throws PersistenceException {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		return configMgr.getInt(ConfigConstants.COLLECTION_SHEET_DAYS_IN_ADVANCE);
	}

}
