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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;

public class SessionHolder {

    private final Session session;
    private Transaction transaction = null;
    private AuditInterceptor interceptor = null;

    public SessionHolder(Session session) {
        this.session = session;
        if (session == null) {
            throw new NullPointerException("session is required");
        }
        // interceptor = new AuditInterceptor();
    }

    public Transaction startTransaction() {
        if (transaction == null) {
            transaction = session.beginTransaction();
        }

        return transaction;
    }

    public void setTranasction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Session getSession() {
        return session;
    }

    public void setInterceptor(AuditInterceptor auditInterceptor) {
        this.interceptor = auditInterceptor;
    }

    public AuditInterceptor getInterceptor() {
        return interceptor;
    }

    public void close() {
    }
}
