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

package org.mifos.framework.components.audit.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class AuditPersistence extends Persistence {

    public void save(AuditLog auditLog) {
        Session session = null;
        Transaction txn = null;
        try {
            session = StaticHibernateUtil.openSession();
            txn = session.beginTransaction();
            session.save(auditLog);
            txn.commit();
        } catch (Exception e) {
            txn.rollback();
            throw new RuntimeException(e);
        } finally {
            try {
                StaticHibernateUtil.closeSession(session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<AuditLog> getAuditLogRecords(Short entityType, Integer entityId) throws PersistenceException {
        Map<Object, Object> queryParameter = new HashMap<Object, Object>();
        queryParameter.put("entityType", entityType);
        queryParameter.put("entityId", entityId);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_AUDIT_LOG_RECORD, queryParameter);
    }

}
