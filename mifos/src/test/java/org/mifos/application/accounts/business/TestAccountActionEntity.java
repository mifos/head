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
 
package org.mifos.application.accounts.business;

import org.hibernate.Session;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * Also see {@link AccountActionEntityTest}.
 */
public class TestAccountActionEntity extends MifosIntegrationTest {	
	
	public TestAccountActionEntity() throws SystemException, ApplicationException {
        super();
    }

    private Session session;
	private AccountActionEntity accountActionEntity;

	@Override
	protected void setUp() throws Exception {
		session = StaticHibernateUtil.getSessionTL();
	}

	@Override
	protected void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		session=null;
	}

	public void testGetAccountAction(){
		Short id = 1;
		accountActionEntity = getAccountActionEntityObject(id);
		assertEquals("Loan Repayment", accountActionEntity.getName());
	}

	private AccountActionEntity getAccountActionEntityObject(Short id) {
		return (AccountActionEntity)session.get(AccountActionEntity.class,id);
	}
	
}
