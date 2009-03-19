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

package org.mifos.framework.hibernate.helper;

import java.sql.Connection;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.exceptions.HibernateProcessException;

public class HibernateUtil {

    public void setThreadLocal(SessionHolder holder) {
        StaticHibernateUtil.setThreadLocal(holder);
    }
    
    public void resetDatabase() {
        StaticHibernateUtil.resetDatabase();
    }

    public Session openSession() throws HibernateProcessException {
        return StaticHibernateUtil.openSession();
    }

    public void closeSession(Session session) throws HibernateProcessException {
        StaticHibernateUtil.closeSession(session);
    }

    public SessionFactory getSessionFactory() {
        return StaticHibernateUtil.getSessionFactory();
    }

    public Session openSession(Connection connection) {
        return StaticHibernateUtil.openSession(connection);
    }

    public Session getSessionTL() {
        return StaticHibernateUtil.getSessionTL();
    }

    public AuditInterceptor getInterceptor() {
        return StaticHibernateUtil.getInterceptor();
    }

    public Transaction startTransaction() {
        return StaticHibernateUtil.startTransaction();
    }

    public Transaction getTransaction() {
        return StaticHibernateUtil.getTransaction();
    }

    public void closeSession() {
        StaticHibernateUtil.closeSession();
    }

    public void flushAndCloseSession() {
        StaticHibernateUtil.flushAndCloseSession();
    }

    public void flushAndClearSession() {
        StaticHibernateUtil.flushAndClearSession();
    }

    public SessionHolder getSessionHolder() {
        return StaticHibernateUtil.getSessionHolder();
    }

    public SessionHolder getOrCreateSessionHolder() throws HibernateException {
        return StaticHibernateUtil.getOrCreateSessionHolder();
    }

    public boolean isSessionOpen() {
        return StaticHibernateUtil.isSessionOpen();
    }

    public void commitTransaction() {
        StaticHibernateUtil.commitTransaction();
    }

    public void rollbackTransaction() {
        StaticHibernateUtil.rollbackTransaction();
    }

}
