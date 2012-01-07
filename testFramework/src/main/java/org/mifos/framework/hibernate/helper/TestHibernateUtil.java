package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Properties;

@SuppressWarnings("PMD")
public class TestHibernateUtil extends HibernateUtil {
    private HibernateUtil hibernateUtil;

    public TestHibernateUtil(HibernateUtil hibernateUtil) {
        super(null, null);
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
    public Object getInterceptor() {
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
}
