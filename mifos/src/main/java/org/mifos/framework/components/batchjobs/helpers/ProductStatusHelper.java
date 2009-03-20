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

import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class ProductStatusHelper extends TaskHelper {

	public ProductStatusHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws BatchJobException {
		Session session;
		String hqlUpdate;
		Query query;
		try {
			session = StaticHibernateUtil.getSessionTL();
			StaticHibernateUtil.startTransaction();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:activeLoanStatus "
					+ "where p.prdType.productTypeID=:loan and p.startDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("activeLoanStatus", PrdStatus.LOAN_ACTIVE.getValue());
			query.setShort("loan", ProductType.LOAN.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpdate();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:inActiveLoanStatus "
					+ "where p.prdType.productTypeID=:loan and p.endDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("inActiveLoanStatus", PrdStatus.LOAN_INACTIVE
					.getValue());
			query.setShort("loan", ProductType.LOAN.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpdate();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:activeSavingStatus "
					+ "where p.prdType.productTypeID=:saving and p.startDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("activeSavingStatus", PrdStatus.SAVINGS_ACTIVE
					.getValue());
			query.setShort("saving", ProductType.SAVINGS.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpdate();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:inActiveSavingStatus "
					+ "where p.prdType.productTypeID=:saving and p.endDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("inActiveSavingStatus", PrdStatus.SAVINGS_INACTIVE
					.getValue());
			query.setShort("saving", ProductType.SAVINGS.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpdate();

			StaticHibernateUtil.commitTransaction();
		} catch (Exception e) {
			try {
				StaticHibernateUtil.rollbackTransaction();
			} catch (Exception ex) {
				// Whoops, rollback failed, log error?
				MifosLogManager
				.getLogger(LoggerConstants.BATCH_JOBS)
				.error("ProductStatusHelper execute failed and subsequent rollback failed with exception " + ex.getClass().getName() + ": " + ex.getMessage());
			}
			throw new BatchJobException(e);
		}
	}

}
