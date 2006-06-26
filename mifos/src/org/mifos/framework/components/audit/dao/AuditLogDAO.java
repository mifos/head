/**

 * AuditLogDAO.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.framework.components.audit.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.valueobjects.AuditLog;
import org.mifos.framework.components.audit.util.valueobjects.AuditLogRecord;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.ValueObject;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * Has methods to create, get and delete audit log details.
 * @author rajitha
 */
public class AuditLogDAO {

	public void createAuditLog(ValueObject valueObject) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.getSession();
			txn = session.beginTransaction();
			session.save((AuditLog) valueObject);
			txn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Collection getAuditLog(long featureId) {// Yet to implement
		return null;
	}

	public void deleteAuditLog(long featureId, Calendar startDate,
			Calendar endDate) {
	}

}