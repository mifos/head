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
 
package org.mifos.application.customer.business;

import java.sql.Date;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerTrxnDetailEntity extends MifosIntegrationTest {

	public TestCustomerTrxnDetailEntity() throws SystemException, ApplicationException {
        super();
    }

    private AccountBO accountBO = null;
	private CustomerBO center=null;
	private CustomerBO group=null;
	private CustomerBO client=null;
	private UserContext userContext;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
				
		try {
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		
		StaticHibernateUtil.closeSession();
	}
	
	public static void addFeesTrxnDetail(CustomerTrxnDetailEntity accountTrxnEntity,FeesTrxnDetailEntity feeTrxn) {
		accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
	}
	
	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		// TODO: Is CLIENT_ACTIVE right or should this be GROUP_ACTIVE?
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", 
				CustomerStatus.CLIENT_ACTIVE, 
				center);
		client = TestObjectFactory.createClient("Client_Active_test",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	public void testGenerateReverseTrxn() throws Exception {
		userContext = TestUtils.makeUser();
		createInitialObjects();
		accountBO=client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
		customerAccountBO.setUserContext(userContext);
		
		CustomerScheduleEntity accountAction =(CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short.valueOf("1"));
		accountAction.setMiscFeePaid(TestObjectFactory.getMoneyForMFICurrency(100));
		accountAction.setMiscPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(100));
		accountAction.setPaymentDate(currentDate);
		accountAction.setPaymentStatus(PaymentStatus.PAID);
		
		MasterPersistence masterPersistenceService = 
			new MasterPersistence();
		
		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(accountBO,TestObjectFactory.getMoneyForMFICurrency(100),"1111",currentDate,new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));
		
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(
				accountPaymentEntity,
				(AccountActionEntity) masterPersistenceService
				.getPersistentObject(AccountActionEntity.class,AccountActionTypes.PAYMENT.getValue()), Short.valueOf("1"),
				accountAction.getActionDate(), TestObjectFactory.getPersonnel(userContext.getId()),
				currentDate, TestObjectFactory.getMoneyForMFICurrency(200), 
				"payment done", null,
				TestObjectFactory.getMoneyForMFICurrency(100), TestObjectFactory.getMoneyForMFICurrency(100));
		
		
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity:accountAction.getAccountFeesActionDetails()) {
			TestCustomerAccountBO.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,TestObjectFactory.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(
					accountTrxnEntity, accountFeesActionDetailEntity.getAccountFee(),
					accountFeesActionDetailEntity.getFeeAmount());
			accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
		}
		accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
		TestAccountPaymentEntity.addAccountPayment(accountPaymentEntity,customerAccountBO);
		
		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		customerAccountBO= TestObjectFactory.getObject(CustomerAccountBO.class,customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();
		PersonnelBO loggedInUser = new PersonnelPersistence().getPersonnel(userContext.getId());
		for(AccountTrxnEntity accntTrxn : customerAccountBO.getLastPmnt().getAccountTrxns()){
			AccountTrxnEntity reverseAccntTrxn = ((CustomerTrxnDetailEntity)accntTrxn).generateReverseTrxn(loggedInUser, "adjustment");
			assertEquals(reverseAccntTrxn.getAmount(),accntTrxn.getAmount().negate());
			assertEquals(loggedInUser.getPersonnelId(), reverseAccntTrxn.getPersonnel().getPersonnelId());
		}
		
	}

}
