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

import org.hibernate.Query;
import org.joda.time.LocalDate;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class LoanArrearsAgingHelper extends TaskHelper {

    public LoanArrearsAgingHelper(MifosTask mifosTask) {
        super(mifosTask);
    }

    @Override
    public void execute(long timeInMillis) throws BatchJobException {
        /*
         * This batch task expects batch task LoanArrearsTask to have successfully completed. LoanArrearsTask sets loan
         * accounts into bad standing if they are in arrears more than the lateness_days value on prd_type (prd_type_id
         * = 1)
         *
         * This batch task regenerates the contents of table loan_arrears_aging for accounts in bad standing.
         * loan_arrears_aging is then further used by the branch report batch task to extract data for eventual use by
         * the mifos branch report.
         *
         * A database view could replace this batch job. Unfortunately, as of May 2010, MySql views perform extremely
         * badly if the view contains 'grouped' data i.e. algorithm temptable is used rather than algorithm merge.
         * http://dev.mysql.com/doc/refman/5.0/en/view-algorithms.html
         */
        deleteAllLoanArrearsAging();

        generateLoanArrearsAging();
    }

    private void deleteAllLoanArrearsAging() throws BatchJobException {
        StaticHibernateUtil.startTransaction();
        try {
            Query delete = StaticHibernateUtil.getSessionTL().getNamedQuery("deleteAllLoanArrearsAging");
            delete.executeUpdate();
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BatchJobException(e);
        }
    }

    private void generateLoanArrearsAging() throws BatchJobException {
        StaticHibernateUtil.startTransaction();
        try {
            Query insert_select = StaticHibernateUtil.getSessionTL().getNamedQuery("generateLoanArrearsAging");
            insert_select.setParameter("CURRENT_DATE", new LocalDate().toString());
            insert_select.executeUpdate();
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BatchJobException(e);
        }
    }

    @Override
    public boolean isTaskAllowedToRun() {
        return true;
    }
}
