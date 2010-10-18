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

import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import javax.transaction.Synchronization;

public class AuditTransactionForTests implements Transaction {
    public void begin() throws HibernateException {
    }

    public void commit() throws HibernateException {
    }

    public void rollback() throws HibernateException {
    }

    public boolean wasRolledBack() throws HibernateException {
        return false;
    }

    public boolean wasCommitted() throws HibernateException {
        return true;
    }

    public boolean isActive() throws HibernateException {
        return true;
    }

    public void registerSynchronization(Synchronization synchronization) throws HibernateException {
    }

    public void setTimeout(int i) {
    }
}
