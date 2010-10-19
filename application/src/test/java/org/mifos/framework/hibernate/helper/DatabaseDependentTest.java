package org.mifos.framework.hibernate.helper;

import org.hibernate.Session;
import org.junit.Ignore;

@Ignore
public class DatabaseDependentTest {
    public static void before() {
        StaticHibernateUtil.initialize();
        StaticHibernateUtil.setHibernateUtil(new TestHibernateUtil(HibernateUtil.getInstance()));
        HibernateUtil.getInstance().closeSession();
        StaticHibernateUtil.startTransaction();
    }

    public static void after() {
        Session sessionTL = StaticHibernateUtil.getSessionTL();
        if (sessionTL.isOpen()) {
            sessionTL.clear();
            StaticHibernateUtil.rollbackTransaction();
            HibernateUtil.getInstance().closeSession();
        }
    }
}
