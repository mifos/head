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

package org.mifos.framework.hibernate.helper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.mifos.core.ClasspathResource;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.exceptions.ConnectionNotFoundException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.FilePaths;

public class HibernateUtil {

    private static Boolean initialized = Boolean.FALSE;
    private static SessionFactory sessionFactory;
    private static AnnotationConfiguration config = null;
    private static final ThreadLocal<AuditInterceptor> interceptorTL = new ThreadLocal<AuditInterceptor>();
    private static final ThreadLocal<Session> sessionTL = new ThreadLocal<Session>();

    public HibernateUtil() {
        initialize();
    }

    public void initialize() throws HibernateStartUpException {
        synchronized (initialized) {
            if (initialized == Boolean.FALSE) {
                config = new AnnotationConfiguration();
                initializeHibernateConfiguration();
                initializeDatabaseConnnectionSettings();
                sessionFactory = config.buildSessionFactory();
                initialized = Boolean.TRUE;
            }
        }
    }

    public void resetDatabase() {
        closeSession();
    }

    /**
     * Open a new Hibernate session.
     */
    public Session openSession() throws HibernateProcessException {
        try {
            return sessionFactory.openSession();
        } catch (HibernateException e) {
            throw new HibernateProcessException(HibernateConstants.FAILED_OPENINGSESSION, e);
        }
    }

    /**
     * Close a session. Do nothing if the session is null or already closed.
     */
    public void closeSession(Session session) throws HibernateProcessException {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (HibernateException e) {
            throw new HibernateProcessException(HibernateConstants.FAILED_CLOSINGSESSION, e);
        }
    }

    /**
     * Return the hibernate session factory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Return the current hibernate session for this thread. If this thread
     * doesn't have one, create a new one.
     */
    public Session getSessionTL() {
        try {
            getOrCreateSession();
        } catch (HibernateException he) {
            throw new ConnectionNotFoundException(he);
        }
        return sessionTL.get();

    }

    public AuditInterceptor getInterceptor() {
        return interceptorTL.get();
    }

    /**
     * Begin a transaction and store it in a thread-local variable, or return
     * the currently open transaction if there is one. The result is that the
     * transaction will remain open until a call to {@link #commitTransaction()}
     * or {@link #rollbackTransaction()}. Calling this method instead of just
     * creating a hibernate Transaction via the session is probably not a good
     * idea (see an example of the latter at
     * TestObjectPersistence#update(Session,
     * org.mifos.framework.business.PersistentObject)
     */
    public Transaction startTransaction() {
        Transaction transaction = getSessionTL().getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return transaction;
    }

    public Transaction getTransaction() {
        return getSessionTL().getTransaction();
    }

    public void closeSession() {
        if (getSessionTL().isOpen()) {
            getSessionTL().close();
        }
        sessionTL.set(null);
    }

    public void flushAndCloseSession() {
        if (getSessionTL().isOpen()) {
            getSessionTL().flush();
            getSessionTL().close();
        }
        sessionTL.set(null);

    }

    public void flushAndClearSession() {
        Session session = getSessionTL();
        if (session.isOpen()) {
            session.flush();
            session.clear();
        }

    }

    private Session getSession() {
        if (null == sessionTL.get()) {
            // need to log to indicate that the session is being invoked when
            // not present

        }
        return sessionTL.get();
    }

    private Session getOrCreateSession() throws HibernateException {
        if (sessionTL.get() == null) {
            interceptorTL.set(new AuditInterceptor());
            Session session = sessionFactory.openSession(interceptorTL.get());
            sessionTL.set(session);
        }
        return sessionTL.get();
    }

    public boolean isSessionOpen() {
        if (getSession() != null) {
            return getSession().isOpen();
        }
        return false;
    }

    public void commitTransaction() {
        if (getSessionTL().getTransaction().isActive()) {
            getSessionTL().getTransaction().commit();
        }
    }

    public void rollbackTransaction() {
        if (getSessionTL().getTransaction().isActive()) {
            getSessionTL().getTransaction().rollback();
        }
    }

    private void initializeHibernateConfiguration() {
        try {
            config.configure(ClasspathResource.getURI(FilePaths.HIBERNATECFGFILE).toURL());
        } catch (HibernateException e) {
            throw new HibernateStartUpException(e);
        } catch (MalformedURLException e) {
            throw new HibernateStartUpException(e);
        } catch (URISyntaxException e) {
            throw new HibernateStartUpException(e);
        }
    }

    private void initializeDatabaseConnnectionSettings() {
        try {
            config.setProperties(new StandardTestingService().getDatabaseConnectionSettings());
            config.setNamingStrategy(new ImprovedNamingStrategy());
        } catch (IOException e) {
            throw new HibernateStartUpException(e);
        }
    }
}
