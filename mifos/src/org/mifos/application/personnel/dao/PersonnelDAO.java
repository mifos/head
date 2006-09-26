/**

 * PersonnelDAO.java    version: xxx

 

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

package org.mifos.application.personnel.dao;

import org.hibernate.Session;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * This class denotes the DAO layer for the Personnel module.
 */
public class PersonnelDAO extends DAO {

	/**
	 * This method finds user based on user id (auto-generated running number).
	 * 
	 * @param userId
	 * @return instance of Personnel if found, otherwise null
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public Personnel getUser(short userId) throws ApplicationException,
			SystemException {
		Personnel personnel = null;
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			personnel = (Personnel) session
					.get(
							org.mifos.application.personnel.util.valueobjects.Personnel.class,
							userId);
			if (personnel == null)
				return null;
			// get office
			personnel.getOffice().getGlobalOfficeNum();
			// get language
			if (personnel.getPreferredLocale() != null) {
				personnel.getPreferredLocale().getLanguage().getLanguageName();
			}

		} catch (HibernateProcessException hpe) {
			throw hpe;
		} finally {
			HibernateUtil.closeSession(session);
		}
		return personnel;
	}

}
