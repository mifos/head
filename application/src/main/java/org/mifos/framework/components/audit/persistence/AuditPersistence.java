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

package org.mifos.framework.components.audit.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class AuditPersistence extends Persistence {

    public void save(AuditLog auditLog) {
        try {
            //explicit close session calls required to avoid infinite looping of audit logs afterTransaction
            StaticHibernateUtil.closeSession();
            Session session = StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
            session.save(auditLog);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            StaticHibernateUtil.closeSession();
            throw new RuntimeException(e);
        }
    }

    public List<AuditLog> getAuditLogRecords(Short entityType, Integer entityId) throws PersistenceException {
        Map<Object, Object> queryParameter = new HashMap<Object, Object>();
        queryParameter.put("entityType", entityType);
        queryParameter.put("entityId", entityId);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_AUDIT_LOG_RECORD, queryParameter);
    }

}
