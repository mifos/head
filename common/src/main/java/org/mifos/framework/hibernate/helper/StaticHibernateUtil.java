/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mifos.framework.exceptions.HibernateStartUpException;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="DC", justification="Legacy code to be removed")
public class StaticHibernateUtil {

    private static HibernateUtil hibernateUtil;
    private static final Object lockObject = new Object();

    public static void setHibernateUtil(HibernateUtil hibernateUtil) {
        StaticHibernateUtil.hibernateUtil = hibernateUtil;
    }

    public static void initialize(InterceptorFactory interceptorFactory, SessionFactory sessionFactory) throws HibernateStartUpException {
        if(hibernateUtil == null){
            synchronized (lockObject) {
                if (hibernateUtil == null)
                    hibernateUtil = new HibernateUtil(interceptorFactory, sessionFactory);
            }
        }
    }

    public static HibernateUtil getHibernateUtil() {
        return hibernateUtil;
    }

    public static SessionFactory getSessionFactory() {
        return hibernateUtil.getSessionFactory();
    }

    public static Session getSessionTL() {
        return hibernateUtil.getSessionTL();
    }

    public static EmptyInterceptor getInterceptor() {
        return (EmptyInterceptor) hibernateUtil.getInterceptor();
    }

    public static Transaction startTransaction() {
        return hibernateUtil.startTransaction();
    }


    public static void closeSession() {
        hibernateUtil.closeSession();
    }

    public static void flushSession() {
        hibernateUtil.flushSession();
    }

    public static void flushAndCloseSession() {
        hibernateUtil.flushAndCloseSession();
    }

    public static void flushAndClearSession() {
        hibernateUtil.flushAndClearSession();
    }

    public static void commitTransaction() {
        hibernateUtil.commitTransaction();
    }

    public static void rollbackTransaction() {
        hibernateUtil.rollbackTransaction();
    }

    public static void shutdown() {
        if (null != hibernateUtil) {
            hibernateUtil.shutdown();
        }
    }

    public static void clearSession() {
        hibernateUtil.clearSession();
    }
}
