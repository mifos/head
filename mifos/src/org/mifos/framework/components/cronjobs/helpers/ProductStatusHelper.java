/**

 * ProductStatusHelper.java    version: 1.0

 

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

import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class ProductStatusHelper extends TaskHelper {

	public ProductStatusHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		Session session;
		String hqlUpdate;
		Query query;
		try {
			session = HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:activeLoanStatus "
					+ "where p.prdType.productTypeID=:loan and p.startDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("activeLoanStatus", PrdStatus.LOAN_ACTIVE.getValue());
			query.setShort("loan", ProductType.LOAN.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:inActiveLoanStatus "
					+ "where p.prdType.productTypeID=:loan and p.endDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("inActiveLoanStatus", PrdStatus.LOAN_INACTIVE
					.getValue());
			query.setShort("loan", ProductType.LOAN.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:activeSavingStatus "
					+ "where p.prdType.productTypeID=:saving and p.startDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("activeSavingStatus", PrdStatus.SAVINGS_ACTIVE
					.getValue());
			query.setShort("saving", ProductType.SAVINGS.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();

			hqlUpdate = "update PrdOfferingBO p set p.prdStatus=:inActiveSavingStatus "
					+ "where p.prdType.productTypeID=:saving and p.endDate=:currentDate";
			query = session.createQuery(hqlUpdate);
			query.setShort("inActiveSavingStatus", PrdStatus.SAVINGS_INACTIVE
					.getValue());
			query.setShort("saving", ProductType.SAVINGS.getValue());
			query.setDate("currentDate", new Date(timeInMillis));
			query.executeUpate();

			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			throw new CronJobException(e);
		}
	}

}
