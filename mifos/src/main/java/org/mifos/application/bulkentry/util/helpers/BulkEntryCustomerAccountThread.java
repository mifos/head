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

package org.mifos.application.bulkentry.util.helpers;

import java.sql.Date;
import java.util.List;

import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.LocalizationConverter;

public class BulkEntryCustomerAccountThread implements Runnable {

    List<CustomerAccountView> customerAccounts;

    Short personnelId;

    String recieptId;

    Short paymentId;

    Date receiptDate;

    Date transactionDate;

    List<String> accountNums;

    StringBuffer isActivityDone;

    public BulkEntryCustomerAccountThread(List<CustomerAccountView> customerAccounts, Short personnelId,
            String recieptId, Short paymentId, Date receiptDate, Date transactionDate, List<String> accountNums,
            StringBuffer isActivityDone) {
        this.customerAccounts = customerAccounts;
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
            for (CustomerAccountView customerAccountView : customerAccounts) {
                if (null != customerAccountView) {
                    String amount = customerAccountView.getCustomerAccountAmountEntered();
                    if (null != amount
                            && !LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(amount).equals(0.0)) {
                        try {
                            bulkEntryBusinessService.saveCustomerAccountCollections(customerAccountView, personnelId,
                                    recieptId, paymentId, receiptDate, transactionDate);
                            StaticHibernateUtil.commitTransaction();
                        } catch (ServiceException be) {
                            accountNums.add((String) (be.getValues()[0]));
                            StaticHibernateUtil.rollbackTransaction();
                        } catch (Exception e) {
                            accountNums.add(customerAccountView.getAccountId().toString());
                            StaticHibernateUtil.rollbackTransaction();
                        } finally {
                            StaticHibernateUtil.closeSession();
                        }
                    }
                }
            }
        } finally {
            isActivityDone.append("Done");
        }
    }

}
