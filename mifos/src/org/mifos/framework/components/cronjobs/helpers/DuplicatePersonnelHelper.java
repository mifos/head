/**

 * DuplicatePersonnelHelper.java    version: 1.0

 

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

import java.util.Iterator;

import org.hibernate.Session;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.cronjobs.TaskHelper;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class DuplicatePersonnelHelper extends TaskHelper {

	public DuplicatePersonnelHelper(MifosTask mifosTask) {
		super(mifosTask);
	}

	@Override
	public void execute(long timeInMillis) throws CronJobException {
		Session session = null;
		try {
			// When Government Id is null
			session = HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			String hql = "SELECT A.personnelId  FROM    PersonnelBO A, PersonnelBO B,"
					+ "	PersonnelDetailsEntity C, PersonnelDetailsEntity D	"
					+ "   WHERE  "
					+ "   A.personnelId !=B.personnelId  AND "
					+ "   A.personnelId=C.personnelId AND"
					+ "   B.personnelId = D.personnelId AND"
					+ "   C.governmentIdNumber IS NULL AND"
					+ "   D.governmentIdNumber IS NULL AND"
					+ "   C.name.firstName=D.name.firstName AND "
					+ "   C.name.lastName=D.name.lastName AND"
					+ "   C.dob=D.dob";
			Iterator itr = session.createQuery(hql).iterate();
			while (itr.hasNext()) {
				hql = "update PersonnelBO a set a.status=3 where a.personnelId:=id ";
				session.createQuery(hql).setShort("id", (Short) itr.next())
						.executeUpate();
			}
			// When Government Id is not null
			hql = "SELECT A.personnelId  FROM    PersonnelBO A, PersonnelBO B,"
					+ "	PersonnelDetailsEntity C, PersonnelDetailsEntity D	"
					+ "   WHERE  " + "   A.personnelId !=B.personnelId  AND "
					+ "   A.personnelId=C.personnelId AND"
					+ "   B.personnelId = D.personnelId AND"
					+ "   C.governmentIdNumber IS NOT NULL AND"
					+ "   D.governmentIdNumber IS NOT NULL AND"
					+ "   C.governmentIdNumber=D.governmentIdNumber";
			itr = session.createQuery(hql).iterate();
			while (itr.hasNext()) {
				hql = "update PersonnelBO a set a.status=3 where a.personnelId:=id ";
				session.createQuery(hql).setShort("id", (Short) itr.next())
						.executeUpate();
			}
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction();
			throw new CronJobException(e);
		}
	}

	@Override
	public boolean isTaskAllowedToRun() {
		return true;
	}

}
