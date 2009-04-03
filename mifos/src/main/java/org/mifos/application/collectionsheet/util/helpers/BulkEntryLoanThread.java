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
 
package org.mifos.application.collectionsheet.util.helpers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.collectionsheet.business.service.BulkEntryBusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class BulkEntryLoanThread implements Runnable {

    List<LoanAccountsProductView> accountViews;

    Short personnelId;

    String recieptId;

    Short paymentId;

    Date receiptDate;

    Date transactionDate;

    List<String> accountNums;

    StringBuffer isActivityDone;

    // public Exception exception;

    public BulkEntryLoanThread(List<LoanAccountsProductView> accountViews, Short personnelId, String recieptId,
            Short paymentId, Date receiptDate, Date transactionDate, List<String> accountNums,
            StringBuffer isActivityDone) {
        this.accountViews = accountViews;
        this.personnelId = personnelId;
        this.recieptId = recieptId;
        this.paymentId = paymentId;
        this.receiptDate = receiptDate;
        this.transactionDate = transactionDate;
        this.accountNums = accountNums;
        this.isActivityDone = isActivityDone;
    }

    public void run() {
        try {
            BulkEntryBusinessService bulkEntryBusinessService = new BulkEntryBusinessService();
            if (null != accountViews) {
                bulkEntryBusinessService.saveMultipleLoanAccounts(accountViews, 
                    accountNums, personnelId, recieptId, paymentId, receiptDate, transactionDate);
            }
            /*
             * Commented out until we can figure out what is up with
             * TestBulkEntryAction#SUPPLY_ENTERED_AMOUNT_PARAMETERS } catch
             * (Exception e) { this.exception = e;
             */
        } finally {
            isActivityDone.append("Done");
        }
    }

}
