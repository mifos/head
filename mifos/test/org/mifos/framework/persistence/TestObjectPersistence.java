/**

 * TestObjectPersistence.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */
package org.mifos.framework.persistence;

import org.hibernate.Session;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.fees.business.FeeFrequencyTypeEntity;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * This class is used to persist objects created by TestObjectFactory.
 * @author ashishsm
 *
 */
public class TestObjectPersistence {
	
	
	
	/**
	 * @return - Returns the personnel created by master data scripts.
	 * This record does not have any custom fields or roles associated with it.
	 * If the row does not already exist in the database it returns null.
	 */
	public PersonnelBO getPersonnel(Short personnelId) {
		
		Session session = HibernateUtil.getSessionTL();
		return (PersonnelBO)session.get(PersonnelBO.class,personnelId );
	}
	
	/**
	 * @return - Returns the Non loan officer created by test data scripts.
	 * This record does not have any custom fields or roles associated with it.
	 * If the row does not already exist in the database it returns null.
	 */
	public PersonnelBO getNonLoanOfficer() {
		Session session = HibernateUtil.getSessionTL();
		return (PersonnelBO)session.get(PersonnelBO.class, new Short("2"));
	}
	
	public FeesBO createFees(FeesBO fees) {
		Session session = HibernateUtil.getSessionTL();
		session.save(fees);
		return fees;
	}
	
	/**
	 * This persists any object passed as parameter . It starts a new transaction and commits it
	 * if the insertion was successful.
	 * @param obj
	 * @return
	 */
	public PersistentObject persist(PersistentObject obj) {
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(obj);
		HibernateUtil.getTransaction().commit();
		return obj;
		
	}
	
	/**
	 * It returns a loan prd category.
	 * @return
	 */
	public ProductCategoryBO getLoanPrdCategory() {
		Session session = HibernateUtil.getSessionTL();
		return (ProductCategoryBO)session.get(ProductCategoryBO.class, new Short("1"));
	}
	/**
	 * @return - Returns the  office created by test data scripts.
	 * If the row does not already exist in the database it returns null.
	 * defaults created are 1- Head Office , 2 - Area Office , 3 - BranchOffice.
	 */
	
	public OfficeBO getOffice(Short officeId) {
		Session session = HibernateUtil.getSessionTL();
		return (OfficeBO)session.get(OfficeBO.class, officeId);
	}

	/**
	 * It retrieves a PrdStatus object for the passed offeringStatusId.
	 * @param offeringStatusId
	 * @return
	 */
	public PrdStatusEntity retrievePrdStatus(Short offeringStatusId) {
		Session session = HibernateUtil.getSessionTL();
		return (PrdStatusEntity)session.get(PrdStatusEntity.class, offeringStatusId);
		
	}
	
	public MifosCurrency getCurrency() {
		Session session = HibernateUtil.getSessionTL();
		return (MifosCurrency)session.get(org.mifos.application.master.business.MifosCurrency.class, new Short((short)2));
		
	}
	
	public MifosCurrency getCurrency(Short currencyId) {
		Session session = HibernateUtil.getSessionTL();
		return (MifosCurrency)session.get(org.mifos.application.master.business.MifosCurrency.class, currencyId);
		
	}
	
	public LoanBO getLoanAccount(LoanBO loan) {
		Session session = HibernateUtil.getSessionTL();
		session.save(loan);
		HibernateUtil.getTransaction().commit();
		return loan;
		
	}
	
	public FeeFrequencyTypeEntity getFeeFrequencyType() {
		Session session = HibernateUtil.getSessionTL();
		return (FeeFrequencyTypeEntity)session.get(FeeFrequencyTypeEntity.class, Short.valueOf("1"));
		
	}
	
	public void removeObject(PersistentObject obj) {
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.delete(obj);
		HibernateUtil.getTransaction().commit();
	}

	public void update(PersistentObject obj) {
		Session session = HibernateUtil.getSessionTL();	
		HibernateUtil.startTransaction();
		session.saveOrUpdate(obj);
		HibernateUtil.getTransaction().commit();
		
	}

	public void flushandCloseSession() {
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		
	}

	public Object getObject(Class clazz, Integer pk) {
		return HibernateUtil.getSessionTL().get(clazz, pk);
		
	}
	
	public Object getObject(Class clazz, Short pk) {
		return HibernateUtil.getSessionTL().get(clazz, pk);
		
	}
}
