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

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.service.BusinessRuleException;

public class SavingsIntPostingHelper extends TaskHelper {

    private SavingsServiceFacade savingsServiceFacade = DependencyInjectedServiceLocator.locateSavingsServiceFacade();

    public SavingsIntPostingHelper() {
        super();
    }

    @Override
    public void execute(final long scheduledFireTime) throws BatchJobException {

        LocalDate dateOfBatchJob = new LocalDate(scheduledFireTime);

        List<String> errorList = new ArrayList<String>();
        try {
            this.savingsServiceFacade.batchPostInterestToSavingsAccount(dateOfBatchJob);
        } catch (BusinessRuleException e) {
            errorList.add(e.getMessageKey());
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }
}
