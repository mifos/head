package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;

import java.sql.Connection;
import java.util.Properties;

public class NoActionHibernateUtil extends org.mifos.framework.hibernate.helper.HibernateUtil {
    @Override
    public void clearSession() {
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Class<?> getObjectType() {
        return NoActionHibernateUtil.class;
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
    }

    @Override
    public Properties getHibernateProperties() {
        return (Properties) NotRequiredAnywhereYet();
    }

    private Object NotRequiredAnywhereYet() {
        throw new RuntimeException("Not required anywhere yet. Please put your implementation.");
    }

    @Override
    public SessionFactory getSessionFactory() {
        return (SessionFactory) NotRequiredAnywhereYet();
    }

    @Override
    public Session getSessionTL() {
        return (Session) NotRequiredAnywhereYet();
    }

    @Override
    public AuditInterceptor getInterceptor() {
        return super.getInterceptor();
    }

    @Override
    public Transaction startTransaction() {
        return super.startTransaction();
    }

    @Override
    public void closeSession() {
    }

    @Override
    public void flushSession() {
    }

    @Override
    public void flushAndCloseSession() {
    }

    @Override
    public void flushAndClearSession() {
    }

    @Override
    public void commitTransaction() {
    }

    @Override
    public void rollbackTransaction() {
    }

    @Override
    public HibernateUtil getObject() throws Exception {
        return this;
    }

    @Override
    public Connection getConnection() {
        return (Connection) NotRequiredAnywhereYet();
    }
}
