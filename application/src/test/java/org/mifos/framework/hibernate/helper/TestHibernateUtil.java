package org.mifos.framework.hibernate.helper;

import java.sql.Connection;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;

public class TestHibernateUtil extends HibernateUtil {
    private HibernateUtil hibernateUtil;

    public TestHibernateUtil(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public void commitTransaction() {
        hibernateUtil.flushSession();
    }

    @Override
    public void closeSession() {
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        throw new MifosRuntimeException("Cannot set session factory");
    }

    @Override
    public Properties getHibernateProperties() {
        return hibernateUtil.getHibernateProperties();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return hibernateUtil.getSessionFactory();
    }

    @Override
    public Session getSessionTL() {
        return hibernateUtil.getSessionTL();
    }

    @Override
    public AuditInterceptor getInterceptor() {
        return hibernateUtil.getInterceptor();
    }

    @Override
    public Transaction startTransaction() {
        return hibernateUtil.startTransaction();
    }

    @Override
    public void flushSession() {
        if (hibernateUtil.getSessionTL().isOpen()) {
            hibernateUtil.flushSession();
        }
    }

    @Override
    public void flushAndCloseSession() {
        if (hibernateUtil.getSessionTL().isOpen()) {
            hibernateUtil.flushSession();
        }
    }

    @Override
    public void flushAndClearSession() {
        if (hibernateUtil.getSessionTL().isOpen()) {
            hibernateUtil.flushAndClearSession();
        }
    }

    @Override
    public void rollbackTransaction() {
        hibernateUtil.rollbackTransaction();
    }

    @Override
    public HibernateUtil getObject() throws Exception {
        return hibernateUtil.getObject();
    }

    @Override
    public Class<?> getObjectType() {
        return hibernateUtil.getObjectType();
    }

    @Override
    public boolean isSingleton() {
        return hibernateUtil.isSingleton();
    }

    @Override
    public void clearSession() {
        hibernateUtil.clearSession();
    }

    @Override
    public Connection getConnection() {
        Connection connection = super.getConnection();
        return new TestDbConnection(connection);
    }
}
