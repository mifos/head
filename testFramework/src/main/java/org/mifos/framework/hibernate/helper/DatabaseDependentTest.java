package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Ignore;

@Ignore
public class DatabaseDependentTest {
    public static void before(InterceptorFactory interceptorFactory, SessionFactory sessionFactory) {
        StaticHibernateUtil.initialize(interceptorFactory, sessionFactory);
        HibernateUtil hibernateUtil = new HibernateUtil(interceptorFactory, sessionFactory);
        StaticHibernateUtil.setHibernateUtil(new TestHibernateUtil(hibernateUtil));
        hibernateUtil.closeSession();
        StaticHibernateUtil.startTransaction();
    }

    public static Session beforeWithoutInitialization(InterceptorFactory interceptorFactory, SessionFactory sessionFactory) {
        StaticHibernateUtil.initialize(interceptorFactory, sessionFactory);
        StaticHibernateUtil.setHibernateUtil(new TestHibernateUtil(new HibernateUtil(interceptorFactory, sessionFactory)));
        StaticHibernateUtil.startTransaction();
        return StaticHibernateUtil.getSessionTL();
    }

    public static void after(InterceptorFactory interceptorFactory, SessionFactory sessionFactory) {
        Session sessionTL = StaticHibernateUtil.getSessionTL();
        if (sessionTL.isOpen()) {
            new HibernateUtil(interceptorFactory, sessionFactory).closeSession();
        }
    }
}
