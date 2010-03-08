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

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class ApplyHolidayChangesHelper extends TaskHelper {

    List<HolidayBO> unappliedHolidays;

    public ApplyHolidayChangesHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        List<String> errorList = new ArrayList<String>();

        unappliedHolidays = new ArrayList<HolidayBO>();
        try {
            unappliedHolidays = new HolidayPersistence().getUnAppliedHolidays();
        } catch (Exception e) {
            throw new BatchJobException(e);
        }
        if (unappliedHolidays != null && !unappliedHolidays.isEmpty()) {
            for (HolidayBO holiday : unappliedHolidays) {
                try {
                    handleHolidayApplication(holiday);
                    StaticHibernateUtil.commitTransaction();
                } catch (Exception e) {
                    StaticHibernateUtil.rollbackTransaction();
                    errorList.add(holiday.toString());
                } finally {
                    StaticHibernateUtil.closeSession();
                }
            }
        }
        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    /*
     * FIXME: this should be a single transaction. Was this done with multiple transactions to work around a memory
     * problem?
     */
    private void handleHolidayApplication(HolidayBO holiday) throws Exception {
        HolidayUtils.rescheduleLoanRepaymentDates(holiday);

        StaticHibernateUtil.commitTransaction();

        HolidayUtils.rescheduleSavingDates(holiday);

        StaticHibernateUtil.commitTransaction();

        HolidayUtils.rescheduleCustomerDates(holiday);

        // Change flag for this holiday in the databese
        holiday.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
        new HolidayPersistence().createOrUpdate(holiday);
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }
}
