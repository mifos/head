/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;

public class ApplyCustomerFeeChangesHelper extends TaskHelper {

    public ApplyCustomerFeeChangesHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(@SuppressWarnings("unused") long timeInMillis) throws BatchJobException {
        List<String> errorList = new ArrayList<String>();
        List<Short> updatedFeeIds = new ArrayList<Short>();
        try {
            updatedFeeIds = new FeePersistence().getUpdatedFeesForCustomer();
        } catch (Exception e) {
            errorList.add(e.getMessage());
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }

        for (Short feeId : updatedFeeIds) {
            try {
                FeeBO hydratedFee = new FeePersistence().findFeeById(feeId);
                hydratedFee = new FeePersistence().getFee(feeId, hydratedFee.getFeeType());

                if (!hydratedFee.getFeeChangeType().equals(FeeChangeType.NOT_UPDATED)) {
                    List<AccountBO> accounts = new CustomerPersistence().getCustomerAccountsForFee(hydratedFee
                            .getFeeId());
                    if (accounts != null && accounts.size() > 0) {
                        for (AccountBO account : accounts) {
                            updateAccountFee(account, hydratedFee);
                        }
                    }
                }

                hydratedFee.updateFeeChangeType(FeeChangeType.NOT_UPDATED);
                UserContext userContext = new UserContext();
                userContext.setId(PersonnelConstants.SYSTEM_USER);
                hydratedFee.setUserContext(userContext);

                hydratedFee.save();

                StaticHibernateUtil.commitTransaction();
            } catch (Exception e) {
                StaticHibernateUtil.rollbackTransaction();
                errorList.add("feeId: " + feeId);
            }
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    private void updateAccountFee(AccountBO account, FeeBO feesBO) throws BatchJobException {
        CustomerAccountBO customerAccount = (CustomerAccountBO) account;
        AccountFeesEntity accountFee = account.getAccountFees(feesBO.getFeeId());
        customerAccount.updateFee(accountFee, feesBO);
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }
}
